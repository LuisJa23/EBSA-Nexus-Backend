package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.factories.IncidentAssignmentFactory;
import co.com.ebsa.ebsa_nexus.domain.enums.AssignmentStatus;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;
import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;
import co.com.ebsa.ebsa_nexus.domain.entity.IncidentAssignment;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.*;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewRepository;
import co.com.ebsa.ebsa_nexus.domain.repository.IncidentAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación para gestión de asignaciones de incidentes a cuadrillas.
 * 
 * <p>Implementa la lógica de negocio relacionada con:
 * <ul>
 *   <li>Asignación de incidentes a cuadrillas</li>
 *   <li>Gestión del ciclo de vida de asignaciones (inicio, finalización, cancelación)</li>
 *   <li>Consultas y estadísticas de asignaciones</li>
 *   <li>Validación de reglas de asignación</li>
 * </ul>
 * 
 * <p><b>Reglas de negocio críticas:</b></p>
 * <ul>
 *   <li>Solo cuadrillas disponibles pueden recibir nuevas asignaciones</li>
 *   <li>Una cuadrilla cambia a EN_ATENCION cuando inicia una asignación</li>
 *   <li>Una cuadrilla vuelve a DISPONIBLE cuando completa todas sus asignaciones</li>
 *   <li>Una asignación puede ser cancelada en cualquier momento antes de completarse</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IncidentAssignmentService {
    
    private final IncidentAssignmentRepository assignmentRepository;
    private final CrewRepository crewRepository;
    private final IncidentAssignmentFactory assignmentFactory;
    private final CrewService crewService;
    private final CrewMemberService crewMemberService;
    private final NotificationApplicationService notificationService;
    
    /**
     * Asigna un incidente a una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @param incidentId ID del incidente
     * @param assignedBy ID del usuario que asigna
     * @return Asignación creada
     * @throws CrewNotFoundException si la cuadrilla no existe
     * @throws InvalidCrewStatusException si la cuadrilla no está disponible
     */
    public IncidentAssignment assignIncident(Long crewId, Long incidentId, Long assignedBy) {
        log.info("Assigning incident to crew: crewId={}, incidentId={}, assignedBy={}", crewId, incidentId, assignedBy);
        
        // Validar que la cuadrilla existe y está disponible
        Crew crew = crewRepository.findActiveById(crewId)
                .orElseThrow(() -> new CrewNotFoundException(crewId));
        
        if (!crew.isAvailable()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "assign incident");
        }
        
        // Verificar si el incidente ya tiene una asignación activa
        assignmentRepository.findActiveAssignment(incidentId).ifPresent(existing -> {
            log.warn("Incident already has active assignment: incidentId={}, assignmentId={}", 
                    incidentId, existing.getId());
            throw new IllegalStateException("Incident already has an active assignment");
        });
        
        // Crear y guardar la asignación
        IncidentAssignment assignment = assignmentFactory.createAssignment(crewId, incidentId, assignedBy);
        IncidentAssignment saved = assignmentRepository.save(assignment);
        
        // Notificar a todos los miembros de la cuadrilla
        try {
            List<CrewMember> activeMembers = crewMemberService.getActiveMembers(crewId);
            for (CrewMember member : activeMembers) {
                String roleText = member.isLeader() ? "Tu cuadrilla" : "La cuadrilla";
                notificationService.createNotification(
                    member.getUserId(),
                    "NOVELTY_ASSIGNED",
                    "Nueva Novedad Asignada",
                    String.format("%s '%s' ha sido asignada a un nuevo incidente (ID: %d). Revisa los detalles y coordina con tu equipo.", 
                                 roleText, crew.getName(), incidentId),
                    null
                );
            }
            log.info("Notifications created for {} crew members about incident assignment", activeMembers.size());
        } catch (Exception e) {
            // No fallar la operación si las notificaciones fallan
            log.error("Failed to create notifications for incident assignment: crewId={}, incidentId={}", 
                     crewId, incidentId, e);
        }
        
        log.info("Incident assigned successfully: assignmentId={}, crewId={}, incidentId={}", 
                saved.getId(), crewId, incidentId);
        return saved;
    }
    
    /**
     * Asigna un incidente con notas iniciales.
     * 
     * @param crewId ID de la cuadrilla
     * @param incidentId ID del incidente
     * @param assignedBy ID del usuario que asigna
     * @param notes Notas iniciales
     * @return Asignación creada
     */
    public IncidentAssignment assignIncidentWithNotes(Long crewId, Long incidentId, Long assignedBy, String notes) {
        log.info("Assigning incident with notes to crew: crewId={}, incidentId={}", crewId, incidentId);
        
        // Validar cuadrilla
        Crew crew = crewRepository.findActiveById(crewId)
                .orElseThrow(() -> new CrewNotFoundException(crewId));
        
        if (!crew.isAvailable()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "assign incident");
        }
        
        // Verificar asignación activa
        assignmentRepository.findActiveAssignment(incidentId).ifPresent(existing -> {
            throw new IllegalStateException("Incident already has an active assignment");
        });
        
        // Crear con notas
        IncidentAssignment assignment = assignmentFactory.createAssignmentWithNotes(crewId, incidentId, assignedBy, notes);
        IncidentAssignment saved = assignmentRepository.save(assignment);
        
        // Notificar a todos los miembros de la cuadrilla
        try {
            List<CrewMember> activeMembers = crewMemberService.getActiveMembers(crewId);
            for (CrewMember member : activeMembers) {
                String roleText = member.isLeader() ? "Tu cuadrilla" : "La cuadrilla";
                notificationService.createNotification(
                    member.getUserId(),
                    "NOVELTY_ASSIGNED",
                    "Nueva Novedad Asignada",
                    String.format("%s '%s' ha sido asignada a un nuevo incidente (ID: %d). Revisa los detalles y coordina con tu equipo. Notas: %s", 
                                 roleText, crew.getName(), incidentId, notes),
                    null
                );
            }
            log.info("Notifications created for {} crew members about incident assignment with notes", activeMembers.size());
        } catch (Exception e) {
            // No fallar la operación si las notificaciones fallan
            log.error("Failed to create notifications for incident assignment: crewId={}, incidentId={}", 
                     crewId, incidentId, e);
        }
        
        log.info("Incident assigned with notes successfully: assignmentId={}", saved.getId());
        return saved;
    }
    
    /**
     * Inicia una asignación.
     * Cambia el estado a EN_CURSO y la cuadrilla a EN_ATENCION.
     * 
     * @param assignmentId ID de la asignación
     * @return Asignación actualizada
     * @throws IncidentAssignmentNotFoundException si no se encuentra
     * @throws InvalidAssignmentStatusException si no se puede iniciar
     */
    public IncidentAssignment startAssignment(Long assignmentId) {
        log.info("Starting assignment: assignmentId={}", assignmentId);
        
        IncidentAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IncidentAssignmentNotFoundException(assignmentId));
        
        if (assignment.getStatus() != AssignmentStatus.ASIGNADO) {
            throw new InvalidAssignmentStatusException(assignment.getStatus(), "start assignment");
        }
        
        // Iniciar asignación
        assignment.start();
        IncidentAssignment updated = assignmentRepository.save(assignment);
        
        // Cambiar estado de cuadrilla a EN_ATENCION
        crewService.markAsInAttention(assignment.getCrewId());
        
        log.info("Assignment started successfully: assignmentId={}", assignmentId);
        return updated;
    }
    
    /**
     * Completa una asignación.
     * Cambia el estado a COMPLETADO y marca la cuadrilla como DISPONIBLE
     * si no tiene más asignaciones activas.
     * 
     * @param assignmentId ID de la asignación
     * @return Asignación actualizada
     * @throws IncidentAssignmentNotFoundException si no se encuentra
     * @throws InvalidAssignmentStatusException si no se puede completar
     */
    public IncidentAssignment completeAssignment(Long assignmentId) {
        log.info("Completing assignment: assignmentId={}", assignmentId);
        
        IncidentAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IncidentAssignmentNotFoundException(assignmentId));
        
        if (assignment.getStatus() != AssignmentStatus.EN_CURSO) {
            throw new InvalidAssignmentStatusException(assignment.getStatus(), "complete assignment");
        }
        
        // Completar asignación
        assignment.complete();
        IncidentAssignment updated = assignmentRepository.save(assignment);
        
        // Si no hay más asignaciones activas, marcar cuadrilla como disponible
        boolean hasMore = assignmentRepository.hasOpenAssignments(assignment.getCrewId());
        if (!hasMore) {
            crewService.markAsAvailable(assignment.getCrewId());
            log.info("Crew marked as available: crewId={}", assignment.getCrewId());
        }
        
        log.info("Assignment completed successfully: assignmentId={}", assignmentId);
        return updated;
    }
    
    /**
     * Completa una asignación con notas finales.
     * 
     * @param assignmentId ID de la asignación
     * @param completionNotes Notas de finalización
     * @return Asignación actualizada
     */
    public IncidentAssignment completeAssignmentWithNotes(Long assignmentId, String completionNotes) {
        log.info("Completing assignment with notes: assignmentId={}", assignmentId);
        
        IncidentAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IncidentAssignmentNotFoundException(assignmentId));
        
        if (assignment.getStatus() != AssignmentStatus.EN_CURSO) {
            throw new InvalidAssignmentStatusException(assignment.getStatus(), "complete assignment");
        }
        
        // Agregar notas y completar
        if (completionNotes != null && !completionNotes.trim().isEmpty()) {
            assignment.addNote(completionNotes);
        }
        assignment.complete();
        IncidentAssignment updated = assignmentRepository.save(assignment);
        
        // Verificar otras asignaciones
        boolean hasMore = assignmentRepository.hasOpenAssignments(assignment.getCrewId());
        if (!hasMore) {
            crewService.markAsAvailable(assignment.getCrewId());
        }
        
        log.info("Assignment completed with notes successfully: assignmentId={}", assignmentId);
        return updated;
    }
    
    /**
     * Cancela una asignación.
     * Marca la cuadrilla como DISPONIBLE si no tiene más asignaciones.
     * 
     * @param assignmentId ID de la asignación
     * @param reason Razón de cancelación
     * @return Asignación actualizada
     * @throws IncidentAssignmentNotFoundException si no se encuentra
     * @throws InvalidAssignmentStatusException si no se puede cancelar
     */
    public IncidentAssignment cancelAssignment(Long assignmentId, String reason) {
        log.info("Cancelling assignment: assignmentId={}, reason={}", assignmentId, reason);
        
        IncidentAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IncidentAssignmentNotFoundException(assignmentId));
        
        if (assignment.getStatus() == AssignmentStatus.COMPLETADO || 
            assignment.getStatus() == AssignmentStatus.CANCELADO) {
            throw new InvalidAssignmentStatusException(assignment.getStatus(), "cancel assignment");
        }
        
        // Cancelar con razón
        String cancelReason = (reason != null && !reason.trim().isEmpty()) ? reason : "No reason provided";
        assignment.cancel(cancelReason);
        IncidentAssignment updated = assignmentRepository.save(assignment);
        
        // Verificar otras asignaciones
        boolean hasMore = assignmentRepository.hasOpenAssignments(assignment.getCrewId());
        if (!hasMore) {
            crewService.markAsAvailable(assignment.getCrewId());
            log.info("Crew marked as available after cancellation: crewId={}", assignment.getCrewId());
        }
        
        log.info("Assignment cancelled successfully: assignmentId={}", assignmentId);
        return updated;
    }
    
    /**
     * Agrega notas a una asignación.
     * 
     * @param assignmentId ID de la asignación
     * @param notes Notas a agregar
     * @return Asignación actualizada
     */
    public IncidentAssignment addNotes(Long assignmentId, String notes) {
        log.info("Adding notes to assignment: assignmentId={}", assignmentId);
        
        IncidentAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IncidentAssignmentNotFoundException(assignmentId));
        
        assignment.addNote(notes);
        IncidentAssignment updated = assignmentRepository.save(assignment);
        
        log.info("Notes added successfully: assignmentId={}", assignmentId);
        return updated;
    }
    
    /**
     * Obtiene una asignación por ID.
     * 
     * @param id ID de la asignación
     * @return Asignación encontrada
     * @throws IncidentAssignmentNotFoundException si no se encuentra
     */
    @Transactional(readOnly = true)
    public IncidentAssignment getAssignmentById(Long id) {
        log.debug("Getting assignment by id: {}", id);
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new IncidentAssignmentNotFoundException(id));
    }
    
    /**
     * Obtiene todas las asignaciones de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones
     */
    @Transactional(readOnly = true)
    public List<IncidentAssignment> getAssignmentsByCrew(Long crewId) {
        log.debug("Getting assignments for crew: {}", crewId);
        return assignmentRepository.findByCrew(crewId);
    }
    
    /**
     * Obtiene las asignaciones activas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones activas
     */
    @Transactional(readOnly = true)
    public List<IncidentAssignment> getActiveAssignments(Long crewId) {
        log.debug("Getting active assignments for crew: {}", crewId);
        return assignmentRepository.findActiveAssignments(crewId);
    }
    
    /**
     * Obtiene las asignaciones de un incidente.
     * 
     * @param incidentId ID del incidente
     * @return Lista de asignaciones
     */
    @Transactional(readOnly = true)
    public List<IncidentAssignment> getAssignmentsByIncident(Long incidentId) {
        log.debug("Getting assignments for incident: {}", incidentId);
        return assignmentRepository.findByIncident(incidentId);
    }
    
    /**
     * Obtiene la asignación activa de un incidente.
     * 
     * @param incidentId ID del incidente
     * @return Asignación activa si existe
     */
    @Transactional(readOnly = true)
    public IncidentAssignment getActiveAssignmentByIncident(Long incidentId) {
        log.debug("Getting active assignment for incident: {}", incidentId);
        return assignmentRepository.findActiveAssignment(incidentId)
                .orElseThrow(() -> new IncidentAssignmentNotFoundException(
                        "No active assignment found for incident: " + incidentId));
    }
    
    /**
     * Obtiene asignaciones por estado.
     * 
     * @param status Estado a filtrar
     * @return Lista de asignaciones con ese estado
     */
    @Transactional(readOnly = true)
    public List<IncidentAssignment> getAssignmentsByStatus(AssignmentStatus status) {
        log.debug("Getting assignments by status: {}", status);
        return assignmentRepository.findByStatus(status);
    }
    
    /**
     * Obtiene asignaciones completadas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones completadas
     */
    @Transactional(readOnly = true)
    public List<IncidentAssignment> getCompletedAssignments(Long crewId) {
        log.debug("Getting completed assignments for crew: {}", crewId);
        return assignmentRepository.findCompletedAssignments(crewId);
    }
    
    /**
     * Obtiene asignaciones creadas por un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de asignaciones
     */
    @Transactional(readOnly = true)
    public List<IncidentAssignment> getAssignmentsByUser(Long userId) {
        log.debug("Getting assignments created by user: {}", userId);
        return assignmentRepository.findByAssignedBy(userId);
    }
    
    /**
     * Obtiene asignaciones en un rango de fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de asignaciones en el rango
     */
    @Transactional(readOnly = true)
    public List<IncidentAssignment> getAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting assignments between {} and {}", startDate, endDate);
        return assignmentRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Verifica si una cuadrilla tiene asignaciones abiertas.
     * 
     * @param crewId ID de la cuadrilla
     * @return true si tiene asignaciones abiertas
     */
    @Transactional(readOnly = true)
    public boolean hasOpenAssignments(Long crewId) {
        return assignmentRepository.hasOpenAssignments(crewId);
    }
    
    /**
     * Cuenta asignaciones activas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de asignaciones activas
     */
    @Transactional(readOnly = true)
    public long countActiveAssignments(Long crewId) {
        return assignmentRepository.countActiveAssignments(crewId);
    }
    
    /**
     * Cuenta asignaciones completadas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de asignaciones completadas
     */
    @Transactional(readOnly = true)
    public long countCompletedAssignments(Long crewId) {
        return assignmentRepository.countCompletedAssignments(crewId);
    }
}
