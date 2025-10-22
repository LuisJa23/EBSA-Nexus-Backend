package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad Novelty.
 * Define las operaciones de persistencia sin depender de tecnologías específicas.
 * 
 * <p>Esta interfaz pertenece a la capa de dominio y será implementada
 * por la capa de infraestructura.</p>
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public interface NoveltyRepository {
    
    /**
     * Guarda una novedad (crear o actualizar).
     * 
     * @param novelty Novedad a guardar
     * @return Novedad guardada con ID asignado
     * @throws IllegalArgumentException si novelty es null
     */
    Novelty save(Novelty novelty);
    
    /**
     * Busca una novedad por su ID.
     * 
     * @param id ID de la novedad
     * @return Optional con la novedad si existe
     * @throws IllegalArgumentException si id es null
     */
    Optional<Novelty> findById(Long id);
    
    /**
     * Obtiene todas las novedades con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de novedades
     */
    Page<Novelty> findAll(Pageable pageable);
    
    /**
     * Obtiene novedades filtradas por estado con paginación.
     * 
     * @param status Estado a filtrar
     * @param pageable Configuración de paginación
     * @return Página de novedades con ese estado
     */
    Page<Novelty> findByStatus(NoveltyStatus status, Pageable pageable);
    
    /**
     * Obtiene novedades creadas por un usuario específico.
     * 
     * @param userId ID del usuario creador
     * @param pageable Configuración de paginación
     * @return Página de novedades creadas por ese usuario
     */
    Page<Novelty> findByCreatedById(Long userId, Pageable pageable);
    
    /**
     * Obtiene novedades filtradas por múltiples criterios.
     * 
     * @param status Estado (opcional)
     * @param reason Razón (opcional)
     * @param crewId ID de la cuadrilla (opcional)
     * @param reportedByUserId ID del usuario que reportó (opcional)
     * @param startDate Fecha inicio (opcional)
     * @param endDate Fecha fin (opcional)
     * @param pageable Configuración de paginación
     * @return Página de novedades que cumplen los criterios
     */
    Page<Novelty> findByFilters(
        NoveltyStatus status,
        String reason,
        Long crewId,
        Long reportedByUserId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Obtiene novedades por cuadrilla ordenadas por fecha de creación.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de novedades de la cuadrilla
     */
    List<Novelty> findByCrewIdOrderByCreatedAtDesc(Long crewId);
    
    /**
     * Obtiene novedades por estado ordenadas por fecha de creación.
     * 
     * @param status Estado
     * @return Lista de novedades con ese estado
     */
    List<Novelty> findByStatusOrderByCreatedAtDesc(NoveltyStatus status);
    
    /**
     * Obtiene novedades creadas en un rango de fechas.
     * 
     * @param startDateTime Fecha/hora inicio
     * @param endDateTime Fecha/hora fin
     * @return Lista de novedades en el rango
     */
    List<Novelty> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Obtiene novedades de una cuadrilla en un rango de fechas.
     * 
     * @param crewId ID de la cuadrilla
     * @param startDateTime Fecha/hora inicio
     * @param endDateTime Fecha/hora fin
     * @return Lista de novedades de la cuadrilla en el rango
     */
    List<Novelty> findByCrewIdAndCreatedAtBetween(Long crewId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Cuenta novedades por estado.
     * 
     * @param status Estado a contar
     * @return Número de novedades en ese estado
     */
    long countByStatus(NoveltyStatus status);
    
    /**
     * Verifica si existe una novedad con el ID dado.
     * 
     * @param id ID de la novedad
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);
}
