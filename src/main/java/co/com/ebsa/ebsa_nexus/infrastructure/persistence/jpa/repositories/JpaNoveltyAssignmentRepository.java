package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
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
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.noveltyId = :noveltyId ORDER BY na.assignedAt DESC")
    List<NoveltyAssignment> findByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Busca la última asignación de una novedad.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.noveltyId = :noveltyId ORDER BY na.assignedAt DESC LIMIT 1")
    Optional<NoveltyAssignment> findLatestByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Busca asignaciones de una cuadrilla específica.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.assignedCrewId = :crewId ORDER BY na.assignedAt DESC")
    List<NoveltyAssignment> findByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca asignaciones realizadas por un usuario.
     */
    @Query("SELECT na FROM NoveltyAssignment na WHERE na.assignedByUserId = :userId ORDER BY na.assignedAt DESC")
    List<NoveltyAssignment> findByAssignedByUserId(@Param("userId") Long userId);
    
    /**
     * Cuenta asignaciones de una cuadrilla.
     */
    @Query("SELECT COUNT(na) FROM NoveltyAssignment na WHERE na.assignedCrewId = :crewId")
    long countByCrewId(@Param("crewId") Long crewId);
}
