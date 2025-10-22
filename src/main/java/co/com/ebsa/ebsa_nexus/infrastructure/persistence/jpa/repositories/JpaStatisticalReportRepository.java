package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.domain.entity.StatisticalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad StatisticalReport.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-01-21
 */
@Repository
public interface JpaStatisticalReportRepository extends JpaRepository<StatisticalReport, Long> {
    
    /**
     * Obtiene reportes generados por un usuario específico.
     * 
     * @param generatedByUserId ID del usuario
     * @return Lista de reportes ordenados por fecha descendente
     */
    List<StatisticalReport> findByGeneratedByUserIdOrderByGeneratedAtDesc(Long generatedByUserId);
    
    /**
     * Obtiene reportes para un rango de fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de reportes en el rango
     */
    @Query("SELECT r FROM StatisticalReport r WHERE r.startDate BETWEEN :startDate AND :endDate")
    List<StatisticalReport> findByStartDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    /**
     * Obtiene reportes por tipo.
     * 
     * @param reportType Tipo de reporte
     * @return Lista de reportes del tipo especificado
     */
    List<StatisticalReport> findByReportType(String reportType);
    
    /**
     * Obtiene reportes para una cuadrilla específica.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de reportes de la cuadrilla
     */
    List<StatisticalReport> findByCrewId(Long crewId);
    
    /**
     * Cuenta reportes generados en un período.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Número de reportes generados
     */
    @Query("SELECT COUNT(r) FROM StatisticalReport r WHERE r.generatedAt BETWEEN :startDate AND :endDate")
    long countByGeneratedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
