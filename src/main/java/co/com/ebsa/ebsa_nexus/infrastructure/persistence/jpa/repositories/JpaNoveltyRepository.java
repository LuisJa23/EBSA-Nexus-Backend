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
     * Busca novedades por cuadrilla ordenadas por fecha de creación.
     */
    List<Novelty> findByCrewIdOrderByCreatedAtDesc(Long crewId);
    
    /**
     * Busca novedades por estado ordenadas por fecha de creación.
     */
    List<Novelty> findByStatusOrderByCreatedAtDesc(NoveltyStatus status);
    
    /**
     * Busca novedades creadas en un rango de fechas.
     */
    List<Novelty> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Busca novedades de una cuadrilla en un rango de fechas.
     */
    List<Novelty> findByCrewIdAndCreatedAtBetween(Long crewId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
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
        AND (:reportedByUserId IS NULL OR n.createdBy = :reportedByUserId)
        AND (:startDate IS NULL OR n.createdAt >= :startDate)
        AND (:endDate IS NULL OR n.createdAt <= :endDate)
        ORDER BY n.createdAt DESC
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
     * Busca novedades por estado con paginación.
     */
    Page<Novelty> findByStatus(NoveltyStatus status, Pageable pageable);
    
    /**
     * Busca novedades creadas por un usuario con paginación.
     */
    Page<Novelty> findByCreatedByOrderByCreatedAtDesc(Long createdBy, Pageable pageable);
}
