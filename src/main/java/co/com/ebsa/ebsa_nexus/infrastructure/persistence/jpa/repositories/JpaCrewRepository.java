package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.entities.CrewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para CrewEntity.
 * Proporciona operaciones CRUD y queries personalizados para cuadrillas.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Repository
public interface JpaCrewRepository extends JpaRepository<CrewEntity, Long> {
    
    /**
     * Busca una cuadrilla activa por ID.
     * Una cuadrilla es activa si deleted_at es NULL.
     * 
     * @param id ID de la cuadrilla
     * @return Optional con la cuadrilla si existe y está activa
     */
    @Query("SELECT c FROM CrewEntity c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<CrewEntity> findActiveById(@Param("id") Long id);
    
    /**
     * Busca todas las cuadrillas activas (no eliminadas).
     * 
     * @return Lista de cuadrillas con deleted_at = NULL
     */
    @Query("SELECT c FROM CrewEntity c WHERE c.deletedAt IS NULL")
    List<CrewEntity> findAllActive();
    
    /**
     * Busca cuadrillas activas con un estado específico.
     * 
     * @param status Estado de la cuadrilla (DISPONIBLE o EN_ATENCION)
     * @return Lista de cuadrillas activas con ese estado
     */
    @Query("SELECT c FROM CrewEntity c WHERE c.deletedAt IS NULL AND c.status = :status")
    List<CrewEntity> findByStatusAndActive(@Param("status") String status);
    
    /**
     * Busca cuadrillas creadas por un usuario específico.
     * Incluye tanto activas como eliminadas.
     * 
     * @param createdBy ID del usuario creador
     * @return Lista de cuadrillas creadas por ese usuario
     */
    @Query("SELECT c FROM CrewEntity c WHERE c.createdBy = :createdBy")
    List<CrewEntity> findByCreatedBy(@Param("createdBy") Long createdBy);
    
    /**
     * Busca cuadrillas disponibles para asignaciones.
     * Son cuadrillas activas y en estado DISPONIBLE.
     * 
     * @return Lista de cuadrillas disponibles
     */
    @Query("SELECT c FROM CrewEntity c WHERE c.deletedAt IS NULL AND c.status = 'DISPONIBLE'")
    List<CrewEntity> findAvailableCrews();
    
    /**
     * Cuenta el número de cuadrillas activas.
     * 
     * @return Número de cuadrillas con deleted_at = NULL
     */
    @Query("SELECT COUNT(c) FROM CrewEntity c WHERE c.deletedAt IS NULL")
    long countActiveCrews();
    
    /**
     * Cuenta cuadrillas con un estado específico.
     * Solo cuenta activas (no eliminadas).
     * 
     * @param status Estado a contar
     * @return Número de cuadrillas activas con ese estado
     */
    @Query("SELECT COUNT(c) FROM CrewEntity c WHERE c.deletedAt IS NULL AND c.status = :status")
    long countByStatusAndActive(@Param("status") String status);
}
