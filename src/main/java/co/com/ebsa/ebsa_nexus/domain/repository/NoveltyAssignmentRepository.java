package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad NoveltyAssignment.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public interface NoveltyAssignmentRepository {
    
    /**
     * Guarda una asignación.
     * 
     * @param assignment Asignación a guardar
     * @return Asignación guardada con ID asignado
     * @throws IllegalArgumentException si assignment es null
     */
    NoveltyAssignment save(NoveltyAssignment assignment);
    
    /**
     * Busca una asignación por su ID.
     * 
     * @param id ID de la asignación
     * @return Optional con la asignación si existe
     */
    Optional<NoveltyAssignment> findById(Long id);
    
    /**
     * Obtiene todas las asignaciones de una novedad (historial completo).
     * 
     * @param noveltyId ID de la novedad
     * @return Lista de asignaciones ordenadas por fecha descendente
     */
    List<NoveltyAssignment> findByNoveltyIdOrderByAssignedAtDesc(Long noveltyId);
    
    /**
     * Find assignment by novelty ID (latest assignment).
     * 
     * @param noveltyId ID de la novedad
     * @return Optional con la última asignación si existe
     */
    default Optional<NoveltyAssignment> findByNoveltyId(Long noveltyId) {
        return findByNoveltyIdOrderByAssignedAtDesc(noveltyId).stream().findFirst();
    }
    
    /**
     * Obtiene la asignación activa de una novedad.
     * 
     * @param noveltyId ID de la novedad
     * @return Optional con la asignación activa si existe
     */
    Optional<NoveltyAssignment> findActiveByNoveltyId(Long noveltyId);
    
    /**
     * Obtiene todas las asignaciones activas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de asignaciones activas
     */
    List<NoveltyAssignment> findActiveByCrewId(Long crewId);
    
    /**
     * Obtiene todas las asignaciones (activas e inactivas) de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de todas las asignaciones
     */
    List<NoveltyAssignment> findByCrewId(Long crewId);
    
    /**
     * Obtiene asignaciones realizadas por un usuario específico.
     * 
     * @param userId ID del usuario que hizo las asignaciones
     * @return Lista de asignaciones
     */
    List<NoveltyAssignment> findByAssignedById(Long userId);
    
    /**
     * Desactiva todas las asignaciones activas de una novedad.
     * Útil para reasignaciones.
     * 
     * @param noveltyId ID de la novedad
     */
    void deactivateAllByNoveltyId(Long noveltyId);
    
    /**
     * Verifica si una novedad tiene asignación activa.
     * 
     * @param noveltyId ID de la novedad
     * @return true si tiene asignación activa, false en caso contrario
     */
    boolean existsActiveByNoveltyId(Long noveltyId);
    
    /**
     * Cuenta asignaciones activas de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de asignaciones activas
     */
    long countActiveByCrewId(Long crewId);
}
