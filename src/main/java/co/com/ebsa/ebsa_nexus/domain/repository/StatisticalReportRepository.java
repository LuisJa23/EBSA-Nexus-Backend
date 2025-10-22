package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.StatisticalReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad StatisticalReport.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-01-21
 */
public interface StatisticalReportRepository {
    
    /**
     * Guarda un reporte estadístico.
     * 
     * @param report Reporte a guardar
     * @return Reporte guardado con ID asignado
     * @throws IllegalArgumentException si report es null
     */
    StatisticalReport save(StatisticalReport report);
    
    /**
     * Busca un reporte por su ID.
     * 
     * @param id ID del reporte
     * @return Optional con el reporte si existe
     */
    Optional<StatisticalReport> findById(Long id);
    
    /**
     * Obtiene reportes generados por un usuario específico.
     * 
     * @param userId ID del usuario
     * @return Lista de reportes generados ordenados por fecha descendente
     */
    List<StatisticalReport> findByGeneratedByUserIdOrderByGeneratedAtDesc(Long userId);
    
    /**
     * Obtiene reportes para un rango de fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de reportes en el rango
     */
    List<StatisticalReport> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Obtiene reportes por tipo.
     * 
     * @param reportType Tipo de reporte (DAILY, WEEKLY, MONTHLY, CUSTOM)
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
     * Verifica si existe un reporte con el ID dado.
     * 
     * @param id ID del reporte
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);
    
    /**
     * Elimina un reporte.
     * 
     * @param id ID del reporte a eliminar
     */
    void deleteById(Long id);
    
    /**
     * Cuenta reportes generados en un período.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Número de reportes generados
     */
    long countByGeneratedAtBetween(LocalDate startDate, LocalDate endDate);
}
