package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio Spring Data JPA para Novelty.
 * Proporciona operaciones CRUD y queries personalizados.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Repository
public interface JpaNoveltyRepository extends JpaRepository<Novelty, Long> {
    
    /**
     * Busca novedades por cuadrilla ordenadas por fecha de reporte.
     */
    List<Novelty> findByCrewIdOrderByReportedAtDesc(Long crewId);
    
    /**
     * Busca novedades por estado ordenadas por fecha de reporte.
     */
    List<Novelty> findByStatusOrderByReportedAtDesc(NoveltyStatus status);
    
    /**
     * Busca novedades reportadas en un rango de fechas.
     */
    List<Novelty> findByReportedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Busca novedades de una cuadrilla en un rango de fechas.
     */
    List<Novelty> findByCrewIdAndReportedAtBetween(Long crewId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Cuenta novedades por estado.
     */
    long countByStatus(NoveltyStatus status);
    
    /**
     * Búsqueda con filtros múltiples.
     */
    @Query("""
        SELECT n FROM Novelty n 
        WHERE (:status IS NULL OR n.status = :status)
        AND (:reason IS NULL OR n.reason = :reason)
        AND (:crewId IS NULL OR n.crewId = :crewId)
        AND (:reportedByUserId IS NULL OR n.reportedByUserId = :reportedByUserId)
        AND (:startDate IS NULL OR n.reportedAt >= :startDate)
        AND (:endDate IS NULL OR n.reportedAt <= :endDate)
        ORDER BY n.reportedAt DESC
        """)
    Page<Novelty> findByFilters(
        @Param("status") NoveltyStatus status,
        @Param("reason") String reason,
        @Param("crewId") Long crewId,
        @Param("reportedByUserId") Long reportedByUserId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Busca una novedad por su UUID único.
     */
    @Query("SELECT n FROM Novelty n WHERE n.noveltyUuid = :uuid")
    java.util.Optional<Novelty> findByNoveltyUuid(@Param("uuid") String uuid);
    
    /**
     * Verifica si existe una novedad con el UUID dado.
     */
    boolean existsByNoveltyUuid(String uuid);
    
    /**
     * Busca novedades por estado con paginación.
     */
    Page<Novelty> findByStatus(NoveltyStatus status, Pageable pageable);
    
    /**
     * Busca novedades por área con paginación.
     */
    @Query("SELECT n FROM Novelty n WHERE n.area.id = :areaId ORDER BY n.reportedAt DESC")
    Page<Novelty> findByAreaId(@Param("areaId") Long areaId, Pageable pageable);
    
    /**
     * Busca novedades creadas por un usuario con paginación.
     */
    Page<Novelty> findByReportedByUserIdOrderByReportedAtDesc(Long reportedByUserId, Pageable pageable);
    
    /**
     * Cuenta novedades por área.
     */
    @Query("SELECT COUNT(n) FROM Novelty n WHERE n.area.id = :areaId")
    long countByAreaId(@Param("areaId") Long areaId);
    
    /**
     * Busca novedades asignadas a un usuario (vía cuadrilla).
     */
    @Query("""
        SELECT DISTINCT n FROM Novelty n 
        JOIN NoveltyAssignment na ON na.novelty.id = n.id
        JOIN Crew c ON na.crew.id = c.id
        JOIN CrewMember cm ON cm.crew.id = c.id
        WHERE cm.user.id = :userId AND na.isActive = true
        ORDER BY n.reportedAt DESC
        """)
    Page<Novelty> findNoveltiesAssignedToUser(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Busca novedades offline.
     */
    List<Novelty> findByIsOfflineTrue();
}
