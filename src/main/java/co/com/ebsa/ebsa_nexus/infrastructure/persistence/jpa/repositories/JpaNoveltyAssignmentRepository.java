package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para NoveltyAssignment.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Repository
public interface JpaNoveltyAssignmentRepository extends JpaRepository<NoveltyAssignment, Long> {
    
    /**
     * Busca todas las asignaciones de una novedad ordenadas por fecha.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.novelty.id = :noveltyId ORDER BY na.assignedAt DESC")
    List<NoveltyAssignment> findByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Busca la asignación activa de una novedad.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.novelty.id = :noveltyId AND na.isActive = true")
    Optional<NoveltyAssignment> findActiveByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Busca asignaciones de una cuadrilla específica.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.crew.id = :crewId ORDER BY na.assignedAt DESC")
    List<NoveltyAssignment> findByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca asignaciones activas de una cuadrilla.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.crew.id = :crewId AND na.isActive = true ORDER BY na.assignedAt DESC")
    List<NoveltyAssignment> findActiveByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca asignaciones realizadas por un usuario.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.assignedBy.id = :userId ORDER BY na.assignedAt DESC")
    List<NoveltyAssignment> findByAssignedById(@Param("userId") Long userId);
    
    /**
     * Desactiva todas las asignaciones activas de una novedad.
     */
    @Modifying
    @Query("UPDATE NoveltyAssignment na SET na.isActive = false, na.updatedAt = CURRENT_TIMESTAMP WHERE na.novelty.id = :noveltyId AND na.isActive = true")
    void deactivateAllByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Cuenta asignaciones activas de una cuadrilla.
     */
    @Query("SELECT COUNT(na) FROM NoveltyAssignment na WHERE na.crew.id = :crewId AND na.isActive = true")
    long countActiveByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Verifica si existe una asignación activa para una novedad.
     */
    @Query("SELECT CASE WHEN COUNT(na) > 0 THEN true ELSE false END FROM NoveltyAssignment na WHERE na.novelty.id = :noveltyId AND na.isActive = true")
    boolean existsActiveByNoveltyId(@Param("noveltyId") Long noveltyId);
}
