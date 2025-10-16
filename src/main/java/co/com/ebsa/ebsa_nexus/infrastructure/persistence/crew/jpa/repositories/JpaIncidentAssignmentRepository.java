package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.repositories;

import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities.IncidentAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para IncidentAssignmentEntity.
 * Proporciona operaciones CRUD y queries personalizados para asignaciones.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Repository
public interface JpaIncidentAssignmentRepository extends JpaRepository<IncidentAssignmentEntity, Long> {
    
    /**
     * Busca todas las asignaciones de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.crewId = :crewId ORDER BY ia.assignedAt DESC")
    List<IncidentAssignmentEntity> findByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca asignaciones activas de una cuadrilla.
     * Activas = ASIGNADO o EN_CURSO.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones activas
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.crewId = :crewId AND ia.status IN ('ASIGNADO', 'EN_CURSO')")
    List<IncidentAssignmentEntity> findActiveAssignmentsByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca todas las asignaciones de un incidente.
     * 
     * @param noveltyId ID del incidente
     * @return Lista de asignaciones
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.noveltyId = :noveltyId ORDER BY ia.assignedAt DESC")
    List<IncidentAssignmentEntity> findByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Busca la asignación activa de un incidente.
     * 
     * @param noveltyId ID del incidente
     * @return Optional con la asignación activa
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.noveltyId = :noveltyId AND ia.status IN ('ASIGNADO', 'EN_CURSO')")
    Optional<IncidentAssignmentEntity> findActiveAssignmentByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Verifica si una cuadrilla tiene asignaciones abiertas.
     * 
     * @param crewId ID de la cuadrilla
     * @return true si tiene asignaciones en ASIGNADO o EN_CURSO
     */
    @Query("SELECT CASE WHEN COUNT(ia) > 0 THEN true ELSE false END FROM IncidentAssignmentEntity ia WHERE ia.crewId = :crewId AND ia.status IN ('ASIGNADO', 'EN_CURSO')")
    boolean hasOpenAssignments(@Param("crewId") Long crewId);
    
    /**
     * Busca asignaciones por estado.
     * 
     * @param status Estado de las asignaciones
     * @return Lista de asignaciones con ese estado
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.status = :status ORDER BY ia.assignedAt DESC")
    List<IncidentAssignmentEntity> findByStatus(@Param("status") String status);
    
    /**
     * Busca asignaciones completadas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones completadas
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.crewId = :crewId AND ia.status = 'COMPLETADO' ORDER BY ia.completedAt DESC")
    List<IncidentAssignmentEntity> findCompletedAssignmentsByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Cuenta asignaciones completadas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de asignaciones completadas
     */
    @Query("SELECT COUNT(ia) FROM IncidentAssignmentEntity ia WHERE ia.crewId = :crewId AND ia.status = 'COMPLETADO'")
    int countCompletedAssignmentsByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca asignaciones creadas por un usuario.
     * 
     * @param assignedBy ID del usuario
     * @return Lista de asignaciones
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.assignedBy = :assignedBy ORDER BY ia.assignedAt DESC")
    List<IncidentAssignmentEntity> findByAssignedBy(@Param("assignedBy") Long assignedBy);
    
    /**
     * Busca asignaciones en un rango de fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de asignaciones en el rango
     */
    @Query("SELECT ia FROM IncidentAssignmentEntity ia WHERE ia.assignedAt BETWEEN :startDate AND :endDate ORDER BY ia.assignedAt DESC")
    List<IncidentAssignmentEntity> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Cuenta asignaciones activas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de asignaciones activas
     */
    @Query("SELECT COUNT(ia) FROM IncidentAssignmentEntity ia WHERE ia.crewId = :crewId AND ia.status IN ('ASIGNADO', 'EN_CURSO')")
    int countActiveAssignmentsByCrewId(@Param("crewId") Long crewId);
}
