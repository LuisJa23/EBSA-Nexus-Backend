package co.com.ebsa.ebsa_nexus.domain.crew.repositories;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.CrewMember;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad CrewMember.
 * Define las operaciones de persistencia para membresías de cuadrillas.
 * 
 * <p>Este repositorio maneja tanto miembros activos como históricos,
 * permitiendo rastrear todos los cambios de membresía.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public interface CrewMemberRepository {
    
    /**
     * Guarda un miembro de cuadrilla (crear o actualizar).
     * 
     * @param member Miembro a guardar
     * @return Miembro guardado con ID asignado
     * @throws IllegalArgumentException si member es null
     */
    CrewMember save(CrewMember member);
    
    /**
     * Busca un miembro por su ID.
     * 
     * @param id ID del miembro
     * @return Optional con el miembro si existe
     * @throws IllegalArgumentException si id es null
     */
    Optional<CrewMember> findById(Long id);
    
    /**
     * Obtiene todos los miembros activos de una cuadrilla.
     * Un miembro es activo si left_at es null.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de miembros activos, puede estar vacía
     * @throws IllegalArgumentException si crewId es null
     */
    List<CrewMember> findActiveMembers(Long crewId);
    
    /**
     * Obtiene todos los miembros de una cuadrilla (activos e históricos).
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista completa de miembros
     * @throws IllegalArgumentException si crewId es null
     */
    List<CrewMember> findAllMembers(Long crewId);
    
    /**
     * Busca el jefe actual de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Optional con el miembro que es jefe activo, o Optional.empty()
     * @throws IllegalArgumentException si crewId es null
     */
    Optional<CrewMember> findLeader(Long crewId);
    
    /**
     * Verifica si un usuario está en una cuadrilla activa.
     * Es decir, si tiene alguna membresía con left_at = null.
     * 
     * @param userId ID del usuario
     * @return true si el usuario está en alguna cuadrilla activa, false en caso contrario
     * @throws IllegalArgumentException si userId es null
     */
    boolean isUserInActiveCrew(Long userId);
    
    /**
     * Obtiene la membresía activa de un usuario (si existe).
     * Un usuario solo puede estar en una cuadrilla activa a la vez.
     * 
     * @param userId ID del usuario
     * @return Optional con la membresía activa, o Optional.empty()
     * @throws IllegalArgumentException si userId es null
     */
    Optional<CrewMember> findActiveMembership(Long userId);
    
    /**
     * Obtiene la membresía activa de un usuario en una cuadrilla específica.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario
     * @return Optional con la membresía si existe y está activa
     * @throws IllegalArgumentException si crewId o userId son null
     */
    Optional<CrewMember> findActiveMembership(Long crewId, Long userId);
    
    /**
     * Cuenta el número de miembros activos en una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de miembros con left_at = null
     * @throws IllegalArgumentException si crewId es null
     */
    int countActiveMembers(Long crewId);
    
    /**
     * Cuenta el número de jefes activos en una cuadrilla.
     * Debe ser siempre 0 o 1.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de miembros activos con is_leader = true
     * @throws IllegalArgumentException si crewId es null
     */
    int countActiveLeaders(Long crewId);
    
    /**
     * Obtiene el historial completo de membresías de un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de todas las membresías (activas e históricas)
     * @throws IllegalArgumentException si userId es null
     */
    List<CrewMember> findUserHistory(Long userId);
    
    /**
     * Busca todos los miembros activos que son jefes.
     * Útil para validaciones y reportes.
     * 
     * @return Lista de miembros que son jefes activos
     */
    List<CrewMember> findAllActiveLeaders();
}
