package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyReport;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad NoveltyReport.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public interface NoveltyReportRepository {
    
    /**
     * Guarda un reporte de novedad.
     * 
     * @param report Reporte a guardar
     * @return Reporte guardado con ID asignado
     * @throws IllegalArgumentException si report es null
     */
    NoveltyReport save(NoveltyReport report);
    
    /**
     * Busca un reporte por su ID.
     * 
     * @param id ID del reporte
     * @return Optional con el reporte si existe
     */
    Optional<NoveltyReport> findById(Long id);
    
    /**
     * Busca un reporte por el ID de la novedad.
     * Relación 1:1 - solo puede haber un reporte por novedad.
     * 
     * @param noveltyId ID de la novedad
     * @return Optional con el reporte si existe
     */
    Optional<NoveltyReport> findByNoveltyId(Long noveltyId);
    
    /**
     * Obtiene reportes generados por un usuario específico.
     * 
     * @param userId ID del usuario
     * @return Lista de reportes generados
     */
    List<NoveltyReport> findByGeneratedById(Long userId);
    
    /**
     * Verifica si existe un reporte para una novedad.
     * 
     * @param noveltyId ID de la novedad
     * @return true si existe reporte, false en caso contrario
     */
    boolean existsByNoveltyId(Long noveltyId);
    
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
}
