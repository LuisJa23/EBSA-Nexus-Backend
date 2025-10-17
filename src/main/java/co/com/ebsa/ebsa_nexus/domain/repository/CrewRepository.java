package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad Crew.
 * Define las operaciones de persistencia sin depender de tecnologías específicas.
 * 
 * <p>Esta interfaz pertenece a la capa de dominio y será implementada
 * por la capa de infraestructura.</p>
 * 
 * <p><b>Principio de Inversión de Dependencias:</b> El dominio define el contrato,
 * la infraestructura lo implementa.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public interface CrewRepository {
    
    /**
     * Guarda una cuadrilla (crear o actualizar).
     * 
     * @param crew Cuadrilla a guardar
     * @return Cuadrilla guardada con ID asignado
     * @throws IllegalArgumentException si crew es null
     */
    Crew save(Crew crew);
    
    /**
     * Busca una cuadrilla por su ID.
     * Incluye cuadrillas eliminadas.
     * 
     * @param id ID de la cuadrilla
     * @return Optional con la cuadrilla si existe, Optional.empty() si no
     * @throws IllegalArgumentException si id es null
     */
    Optional<Crew> findById(Long id);
    
    /**
     * Busca una cuadrilla activa por su ID.
     * Excluye cuadrillas con deleted_at != null.
     * 
     * @param id ID de la cuadrilla
     * @return Optional con la cuadrilla si existe y está activa
     * @throws IllegalArgumentException si id es null
     */
    Optional<Crew> findActiveById(Long id);
    
    /**
     * Obtiene todas las cuadrillas activas (no eliminadas).
     * 
     * @return Lista de cuadrillas activas, puede estar vacía
     */
    List<Crew> findAllActive();
    
    /**
     * Obtiene todas las cuadrillas activas con un estado específico.
     * 
     * @param status Estado de la cuadrilla (DISPONIBLE o EN_ATENCION)
     * @return Lista de cuadrillas activas con ese estado
     * @throws IllegalArgumentException si status es null
     */
    List<Crew> findByStatus(CrewStatus status);
    
    /**
     * Obtiene todas las cuadrillas creadas por un usuario específico.
     * Incluye tanto activas como eliminadas.
     * 
     * @param creatorId ID del usuario creador
     * @return Lista de cuadrillas creadas por ese usuario
     * @throws IllegalArgumentException si creatorId es null
     */
    List<Crew> findByCreatedBy(Long creatorId);
    
    /**
     * Obtiene todas las cuadrillas disponibles para asignaciones.
     * Es decir, activas y en estado DISPONIBLE.
     * 
     * @return Lista de cuadrillas disponibles
     */
    List<Crew> findAvailableCrews();
    
    /**
     * Verifica si existe una cuadrilla con el ID dado.
     * 
     * @param id ID de la cuadrilla
     * @return true si existe, false en caso contrario
     * @throws IllegalArgumentException si id es null
     */
    boolean existsById(Long id);
    
    /**
     * Cuenta el número de cuadrillas activas.
     * 
     * @return Número de cuadrillas con deleted_at = null
     */
    long countActiveCrews();
    
    /**
     * Cuenta el número de cuadrillas con un estado específico.
     * Solo cuenta cuadrillas activas (no eliminadas).
     * 
     * @param status Estado a contar
     * @return Número de cuadrillas activas con ese estado
     * @throws IllegalArgumentException si status es null
     */
    long countByStatus(CrewStatus status);
}
