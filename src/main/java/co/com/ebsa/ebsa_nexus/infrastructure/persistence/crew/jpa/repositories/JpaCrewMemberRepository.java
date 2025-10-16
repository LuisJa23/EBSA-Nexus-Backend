package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.repositories;

import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities.CrewMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para CrewMemberEntity.
 * Proporciona operaciones CRUD y queries personalizados para membresías.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Repository
public interface JpaCrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {
    
    /**
     * Busca miembros activos de una cuadrilla.
     * Un miembro es activo si left_at es NULL.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de miembros activos
     */
    @Query("SELECT cm FROM CrewMemberEntity cm WHERE cm.crewId = :crewId AND cm.leftAt IS NULL")
    List<CrewMemberEntity> findActiveMembersByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca todos los miembros de una cuadrilla (activos e históricos).
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista completa de miembros
     */
    @Query("SELECT cm FROM CrewMemberEntity cm WHERE cm.crewId = :crewId ORDER BY cm.joinedAt DESC")
    List<CrewMemberEntity> findAllMembersByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Busca el jefe actual de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Optional con el jefe activo
     */
    @Query("SELECT cm FROM CrewMemberEntity cm WHERE cm.crewId = :crewId AND cm.isLeader = true AND cm.leftAt IS NULL")
    Optional<CrewMemberEntity> findLeaderByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Verifica si un usuario está en alguna cuadrilla activa.
     * 
     * @param userId ID del usuario
     * @return true si tiene membresía activa, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END FROM CrewMemberEntity cm WHERE cm.userId = :userId AND cm.leftAt IS NULL")
    boolean existsActiveMembershipByUserId(@Param("userId") Long userId);
    
    /**
     * Busca la membresía activa de un usuario.
     * 
     * @param userId ID del usuario
     * @return Optional con la membresía activa
     */
    @Query("SELECT cm FROM CrewMemberEntity cm WHERE cm.userId = :userId AND cm.leftAt IS NULL")
    Optional<CrewMemberEntity> findActiveMembershipByUserId(@Param("userId") Long userId);
    
    /**
     * Busca membresía activa específica de un usuario en una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario
     * @return Optional con la membresía
     */
    @Query("SELECT cm FROM CrewMemberEntity cm WHERE cm.crewId = :crewId AND cm.userId = :userId AND cm.leftAt IS NULL")
    Optional<CrewMemberEntity> findActiveMembership(@Param("crewId") Long crewId, @Param("userId") Long userId);
    
    /**
     * Cuenta miembros activos de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de miembros activos
     */
    @Query("SELECT COUNT(cm) FROM CrewMemberEntity cm WHERE cm.crewId = :crewId AND cm.leftAt IS NULL")
    int countActiveMembersByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Cuenta jefes activos de una cuadrilla.
     * Debe ser siempre 0 o 1.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de jefes activos
     */
    @Query("SELECT COUNT(cm) FROM CrewMemberEntity cm WHERE cm.crewId = :crewId AND cm.isLeader = true AND cm.leftAt IS NULL")
    int countActiveLeadersByCrewId(@Param("crewId") Long crewId);
    
    /**
     * Obtiene historial completo de membresías de un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de todas las membresías
     */
    @Query("SELECT cm FROM CrewMemberEntity cm WHERE cm.userId = :userId ORDER BY cm.joinedAt DESC")
    List<CrewMemberEntity> findUserHistory(@Param("userId") Long userId);
    
    /**
     * Busca todos los jefes activos en el sistema.
     * 
     * @return Lista de miembros que son jefes activos
     */
    @Query("SELECT cm FROM CrewMemberEntity cm WHERE cm.isLeader = true AND cm.leftAt IS NULL")
    List<CrewMemberEntity> findAllActiveLeaders();
}
