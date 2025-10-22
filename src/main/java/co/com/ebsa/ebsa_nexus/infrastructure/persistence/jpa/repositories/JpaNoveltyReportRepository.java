package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para NoveltyReport.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Repository
public interface JpaNoveltyReportRepository extends JpaRepository<NoveltyReport, Long> {
    
    /**
     * Busca un reporte por el ID de la novedad.
     */
    @Query("SELECT nr FROM NoveltyReport nr WHERE nr.novelty.id = :noveltyId")
    Optional<NoveltyReport> findByNoveltyId(@Param("noveltyId") Long noveltyId);
    
    /**
     * Busca reportes generados por un usuario.
     */
    @Query("SELECT nr FROM NoveltyReport nr WHERE nr.generatedBy.id = :userId ORDER BY nr.createdAt DESC")
    List<NoveltyReport> findByGeneratedById(@Param("userId") Long userId);
    
    /**
     * Verifica si existe un reporte para una novedad.
     */
    @Query("SELECT CASE WHEN COUNT(nr) > 0 THEN true ELSE false END FROM NoveltyReport nr WHERE nr.novelty.id = :noveltyId")
    boolean existsByNoveltyId(@Param("noveltyId") Long noveltyId);
}
