package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.enums.AssignmentStatus;
import co.com.ebsa.ebsa_nexus.domain.entity.IncidentAssignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad IncidentAssignment.
 * Define las operaciones de persistencia para asignaciones de incidentes a cuadrillas.
 * 
 * <p>Este repositorio permite gestionar el ciclo completo de asignaciones,
 * desde la creación hasta la finalización o cancelación.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public interface IncidentAssignmentRepository {
    
    /**
     * Guarda una asignación (crear o actualizar).
     * 
     * @param assignment Asignación a guardar
     * @return Asignación guardada con ID asignado
     * @throws IllegalArgumentException si assignment es null
     */
    IncidentAssignment save(IncidentAssignment assignment);
    
    /**
     * Busca una asignación por su ID.
     * 
     * @param id ID de la asignación
     * @return Optional con la asignación si existe
     * @throws IllegalArgumentException si id es null
     */
    Optional<IncidentAssignment> findById(Long id);
    
    /**
     * Obtiene todas las asignaciones de una cuadrilla.
     * Incluye asignaciones en cualquier estado.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones, puede estar vacía
     * @throws IllegalArgumentException si crewId es null
     */
    List<IncidentAssignment> findByCrew(Long crewId);
    
    /**
     * Obtiene todas las asignaciones activas de una cuadrilla.
     * Es decir, asignaciones en estado ASIGNADO o EN_CURSO.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones activas
     * @throws IllegalArgumentException si crewId es null
     */
    List<IncidentAssignment> findActiveAssignments(Long crewId);
    
    /**
     * Obtiene todas las asignaciones de un incidente específico.
     * Incluye asignaciones históricas (para ver reasignaciones).
     * 
     * @param incidentId ID del incidente
     * @return Lista de todas las asignaciones de ese incidente
     * @throws IllegalArgumentException si incidentId es null
     */
    List<IncidentAssignment> findByIncident(Long incidentId);
    
    /**
     * Obtiene la asignación activa actual de un incidente.
     * Un incidente solo puede tener una asignación activa a la vez.
     * 
     * @param incidentId ID del incidente
     * @return Optional con la asignación activa, o Optional.empty()
     * @throws IllegalArgumentException si incidentId es null
     */
    Optional<IncidentAssignment> findActiveAssignment(Long incidentId);
    
    /**
     * Verifica si una cuadrilla tiene asignaciones abiertas.
     * Una asignación está abierta si su estado es ASIGNADO o EN_CURSO.
     * 
     * @param crewId ID de la cuadrilla
     * @return true si tiene asignaciones abiertas, false en caso contrario
     * @throws IllegalArgumentException si crewId es null
     */
    boolean hasOpenAssignments(Long crewId);
    
    /**
     * Obtiene todas las asignaciones con un estado específico.
     * 
     * @param status Estado de las asignaciones a buscar
     * @return Lista de asignaciones con ese estado
     * @throws IllegalArgumentException si status es null
     */
    List<IncidentAssignment> findByStatus(AssignmentStatus status);
    
    /**
     * Obtiene las asignaciones completadas por una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones con estado COMPLETADO
     * @throws IllegalArgumentException si crewId es null
     */
    List<IncidentAssignment> findCompletedAssignments(Long crewId);
    
    /**
     * Cuenta el número de asignaciones completadas por una cuadrilla.
     * Útil para métricas y estadísticas.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de asignaciones completadas
     * @throws IllegalArgumentException si crewId es null
     */
    int countCompletedAssignments(Long crewId);
    
    /**
     * Obtiene las asignaciones creadas por un usuario específico.
     * 
     * @param userId ID del usuario que asignó
     * @return Lista de asignaciones creadas por ese usuario
     * @throws IllegalArgumentException si userId es null
     */
    List<IncidentAssignment> findByAssignedBy(Long userId);
    
    /**
     * Obtiene asignaciones dentro de un rango de fechas.
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de asignaciones en ese rango
     * @throws IllegalArgumentException si startDate o endDate son null
     */
    List<IncidentAssignment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Cuenta las asignaciones activas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de asignaciones en ASIGNADO o EN_CURSO
     * @throws IllegalArgumentException si crewId es null
     */
    int countActiveAssignments(Long crewId);
}
