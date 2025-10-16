package co.com.ebsa.ebsa_nexus.application.crew.factories;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.IncidentAssignment;
import co.com.ebsa.ebsa_nexus.domain.crew.enums.AssignmentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Factory para crear instancias de IncidentAssignment con validaciones de reglas de negocio.
 * 
 * <p>Garantiza que todas las asignaciones se crean con:
 * <ul>
 *   <li>Estado inicial ASIGNADO</li>
 *   <li>Fecha de asignación correcta</li>
 *   <li>Validaciones de campos obligatorios</li>
 *   <li>Tracking del usuario que asigna</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Component
public class IncidentAssignmentFactory {
    
    /**
     * Crea una nueva asignación de incidente a cuadrilla.
     * 
     * @param crewId ID de la cuadrilla (obligatorio)
     * @param incidentId ID del incidente (obligatorio)
     * @param assignedBy ID del usuario que asigna (obligatorio)
     * @return Nueva instancia de IncidentAssignment
     * @throws IllegalArgumentException si algún campo obligatorio es inválido
     */
    public IncidentAssignment createAssignment(Long crewId, Long incidentId, Long assignedBy) {
        validateAssignmentCreation(crewId, incidentId, assignedBy);
        
        LocalDateTime now = LocalDateTime.now();
        
        return IncidentAssignment.builder()
                .crewId(crewId)
                .incidentId(incidentId)
                .assignedBy(assignedBy)
                .status(AssignmentStatus.ASIGNADO)
                .assignedAt(now)
                .startedAt(null)
                .completedAt(null)
                .cancelledAt(null)
                .notes(null)
                .build();
    }
    
    /**
     * Crea una asignación con notas iniciales.
     * 
     * @param crewId ID de la cuadrilla
     * @param incidentId ID del incidente
     * @param assignedBy ID del usuario que asigna
     * @param initialNotes Notas iniciales de la asignación
     * @return Nueva instancia de IncidentAssignment
     * @throws IllegalArgumentException si algún campo obligatorio es inválido
     */
    public IncidentAssignment createAssignmentWithNotes(Long crewId, Long incidentId, Long assignedBy, String initialNotes) {
        validateAssignmentCreation(crewId, incidentId, assignedBy);
        
        LocalDateTime now = LocalDateTime.now();
        
        return IncidentAssignment.builder()
                .crewId(crewId)
                .incidentId(incidentId)
                .assignedBy(assignedBy)
                .status(AssignmentStatus.ASIGNADO)
                .assignedAt(now)
                .startedAt(null)
                .completedAt(null)
                .cancelledAt(null)
                .notes(initialNotes != null ? initialNotes.trim() : null)
                .build();
    }
    
    /**
     * Crea una asignación con fecha de asignación personalizada.
     * Útil para migraciones o registros históricos.
     * 
     * @param crewId ID de la cuadrilla
     * @param incidentId ID del incidente
     * @param assignedBy ID del usuario que asigna
     * @param assignedAt Fecha de asignación personalizada
     * @return Nueva instancia de IncidentAssignment
     * @throws IllegalArgumentException si algún campo obligatorio es inválido
     */
    public IncidentAssignment createHistoricalAssignment(Long crewId, Long incidentId, Long assignedBy, LocalDateTime assignedAt) {
        validateAssignmentCreation(crewId, incidentId, assignedBy);
        
        if (assignedAt == null) {
            throw new IllegalArgumentException("Assigned at date cannot be null");
        }
        
        if (assignedAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Assigned at date cannot be in the future");
        }
        
        return IncidentAssignment.builder()
                .crewId(crewId)
                .incidentId(incidentId)
                .assignedBy(assignedBy)
                .status(AssignmentStatus.ASIGNADO)
                .assignedAt(assignedAt)
                .startedAt(null)
                .completedAt(null)
                .cancelledAt(null)
                .notes(null)
                .build();
    }
    
    /**
     * Valida los campos obligatorios para crear una asignación.
     * 
     * @param crewId ID de la cuadrilla
     * @param incidentId ID del incidente
     * @param assignedBy ID del usuario que asigna
     * @throws IllegalArgumentException si alguna validación falla
     */
    private void validateAssignmentCreation(Long crewId, Long incidentId, Long assignedBy) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        
        if (crewId <= 0) {
            throw new IllegalArgumentException("Crew ID must be positive");
        }
        
        if (incidentId == null) {
            throw new IllegalArgumentException("Incident ID cannot be null");
        }
        
        if (incidentId <= 0) {
            throw new IllegalArgumentException("Incident ID must be positive");
        }
        
        if (assignedBy == null) {
            throw new IllegalArgumentException("Assigned by user ID cannot be null");
        }
        
        if (assignedBy <= 0) {
            throw new IllegalArgumentException("Assigned by user ID must be positive");
        }
    }
}
