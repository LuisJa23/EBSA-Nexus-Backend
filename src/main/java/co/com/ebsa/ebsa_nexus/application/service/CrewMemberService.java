package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.factories.CrewMemberFactory;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;
import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.*;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewMemberRepository;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicación para gestión de miembros de cuadrillas.
 * 
 * <p>Implementa la lógica de negocio relacionada con:
 * <ul>
 *   <li>Añadir y remover miembros</li>
 *   <li>Gestión de líderes (promoción/degradación)</li>
 *   <li>Validación de reglas de membresía</li>
 *   <li>Historial de membresías</li>
 * </ul>
 * 
 * <p><b>Reglas de negocio críticas:</b></p>
 * <ul>
 *   <li>Un usuario solo puede estar activo en UNA cuadrilla a la vez</li>
 *   <li>Toda cuadrilla debe tener exactamente 1 líder</li>
 *   <li>No se puede remover al último miembro de una cuadrilla</li>
 *   <li>Solo se pueden modificar miembros si la cuadrilla está DISPONIBLE</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CrewMemberService {
    
    private final CrewMemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberFactory memberFactory;
    
    /**
     * Añade un miembro regular a una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario a añadir
     * @return Membresía creada
     * @throws CrewNotFoundException si la cuadrilla no existe
     * @throws UserAlreadyInCrewException si el usuario ya está en otra cuadrilla
     * @throws InvalidCrewStatusException si la cuadrilla no está disponible
     */
    public CrewMember addMember(Long crewId, Long userId) {
        log.info("Adding member to crew: crewId={}, userId={}", crewId, userId);
        
        // Validar que la cuadrilla existe y está disponible
        Crew crew = crewRepository.findActiveById(crewId)
                .orElseThrow(() -> new CrewNotFoundException(crewId));
        
        if (!crew.allowsMemberModifications()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "add members");
        }
        
        // Validar que el usuario no esté en otra cuadrilla
        if (memberRepository.isUserInActiveCrew(userId)) {
            throw new UserAlreadyInCrewException(userId);
        }
        
        // Crear y guardar la membresía
        CrewMember member = memberFactory.createMember(crewId, userId);
        CrewMember saved = memberRepository.save(member);
        
        log.info("Member added successfully: crewId={}, userId={}", crewId, userId);
        return saved;
    }
    
    /**
     * Añade un líder a una cuadrilla.
     * Valida que no exista otro líder activo.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario a añadir como líder
     * @return Membresía creada con rol de líder
     * @throws CrewNotFoundException si la cuadrilla no existe
     * @throws UserAlreadyInCrewException si el usuario ya está en otra cuadrilla
     * @throws InvalidCrewStatusException si la cuadrilla no está disponible
     * @throws IllegalStateException si ya existe un líder
     */
    public CrewMember addLeader(Long crewId, Long userId) {
        log.info("Adding leader to crew: crewId={}, userId={}", crewId, userId);
        
        // Validar cuadrilla
        Crew crew = crewRepository.findActiveById(crewId)
                .orElseThrow(() -> new CrewNotFoundException(crewId));
        
        if (!crew.allowsMemberModifications()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "add leader");
        }
        
        // Validar que no hay otro líder
        if (memberRepository.countActiveLeaders(crewId) > 0) {
            throw new IllegalStateException("Crew already has a leader. Use promoteToLeader to replace the current leader.");
        }
        
        // Validar que el usuario no esté en otra cuadrilla
        if (memberRepository.isUserInActiveCrew(userId)) {
            throw new UserAlreadyInCrewException(userId);
        }
        
        // Crear y guardar como líder
        CrewMember leader = memberFactory.createLeader(crewId, userId);
        CrewMember saved = memberRepository.save(leader);
        
        log.info("Leader added successfully: crewId={}, userId={}", crewId, userId);
        return saved;
    }
    
    /**
     * Remueve un miembro de una cuadrilla.
     * Marca la membresía como terminada (left_at).
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario a remover
     * @throws CrewMemberNotFoundException si no se encuentra la membresía
     * @throws InvalidCrewStatusException si la cuadrilla no está disponible
     * @throws CannotRemoveLastMemberException si es el último miembro
     * @throws CrewHasNoLeaderException si se intenta remover al único líder sin reemplazo
     */
    public void removeMember(Long crewId, Long userId) {
        log.info("Removing member from crew: crewId={}, userId={}", crewId, userId);
        
        // Validar cuadrilla
        Crew crew = crewRepository.findActiveById(crewId)
                .orElseThrow(() -> new CrewNotFoundException(crewId));
        
        if (!crew.allowsMemberModifications()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "remove members");
        }
        
        // Validar que no es el último miembro
        int activeMembers = memberRepository.countActiveMembers(crewId);
        if (activeMembers <= 1) {
            throw new CannotRemoveLastMemberException(crewId);
        }
        
        // Buscar la membresía activa
        CrewMember member = memberRepository.findActiveMembership(crewId, userId)
                .orElseThrow(() -> new CrewMemberNotFoundException(crewId, userId));
        
        // Si es líder, validar que haya reemplazo
        if (member.isLeader()) {
            int leaderCount = memberRepository.countActiveLeaders(crewId);
            if (leaderCount <= 1) {
                throw new CrewHasNoLeaderException("Cannot remove the only leader. Promote another member first.");
            }
        }
        
        // Marcar como salido
        member.markAsLeft();
        memberRepository.save(member);
        
        log.info("Member removed successfully: crewId={}, userId={}", crewId, userId);
    }
    
    /**
     * Promueve un miembro regular a líder.
     * Degrada al líder actual si existe.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario a promover
     * @return Membresía actualizada
     * @throws CrewMemberNotFoundException si no se encuentra el miembro
     * @throws InvalidCrewStatusException si la cuadrilla no está disponible
     */
    public CrewMember promoteToLeader(Long crewId, Long userId) {
        log.info("Promoting member to leader: crewId={}, userId={}", crewId, userId);
        
        // Validar cuadrilla
        Crew crew = crewRepository.findActiveById(crewId)
                .orElseThrow(() -> new CrewNotFoundException(crewId));
        
        if (!crew.allowsMemberModifications()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "promote to leader");
        }
        
        // Buscar la membresía
        CrewMember member = memberRepository.findActiveMembership(crewId, userId)
                .orElseThrow(() -> new CrewMemberNotFoundException(crewId, userId));
        
        if (member.isLeader()) {
            log.warn("User is already a leader: crewId={}, userId={}", crewId, userId);
            return member;
        }
        
        // Degradar al líder actual si existe
        memberRepository.findLeader(crewId).ifPresent(currentLeader -> {
            log.info("Demoting current leader: userId={}", currentLeader.getUserId());
            currentLeader.demoteFromLeader();
            memberRepository.save(currentLeader);
        });
        
        // Promover al nuevo líder
        member.promoteToLeader();
        CrewMember updated = memberRepository.save(member);
        
        log.info("Member promoted to leader successfully: crewId={}, userId={}", crewId, userId);
        return updated;
    }
    
    /**
     * Degrada un líder a miembro regular.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del líder a degradar
     * @return Membresía actualizada
     * @throws CrewMemberNotFoundException si no se encuentra el líder
     * @throws InvalidCrewStatusException si la cuadrilla no está disponible
     */
    public CrewMember demoteFromLeader(Long crewId, Long userId) {
        log.info("Demoting leader: crewId={}, userId={}", crewId, userId);
        
        // Validar cuadrilla
        Crew crew = crewRepository.findActiveById(crewId)
                .orElseThrow(() -> new CrewNotFoundException(crewId));
        
        if (!crew.allowsMemberModifications()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "demote leader");
        }
        
        // Buscar líder
        CrewMember leader = memberRepository.findActiveMembership(crewId, userId)
                .orElseThrow(() -> new CrewMemberNotFoundException(crewId, userId));
        
        if (!leader.isLeader()) {
            log.warn("User is not a leader: crewId={}, userId={}", crewId, userId);
            return leader;
        }
        
        // Degradar
        leader.demoteFromLeader();
        CrewMember updated = memberRepository.save(leader);
        
        log.info("Leader demoted successfully: crewId={}, userId={}", crewId, userId);
        return updated;
    }
    
    /**
     * Obtiene todos los miembros activos de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de miembros activos
     */
    @Transactional(readOnly = true)
    public List<CrewMember> getActiveMembers(Long crewId) {
        log.debug("Getting active members for crew: {}", crewId);
        return memberRepository.findActiveMembers(crewId);
    }
    
    /**
     * Obtiene el historial completo de miembros de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Lista de todos los miembros (activos e inactivos)
     */
    @Transactional(readOnly = true)
    public List<CrewMember> getAllMembers(Long crewId) {
        log.debug("Getting all members (including history) for crew: {}", crewId);
        return memberRepository.findAllMembers(crewId);
    }
    
    /**
     * Obtiene el líder de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Líder de la cuadrilla
     * @throws CrewHasNoLeaderException si no hay líder
     */
    @Transactional(readOnly = true)
    public CrewMember getLeader(Long crewId) {
        log.debug("Getting leader for crew: {}", crewId);
        return memberRepository.findLeader(crewId)
                .orElseThrow(() -> new CrewHasNoLeaderException(crewId));
    }
    
    /**
     * Obtiene el historial de membresías de un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de membresías del usuario
     */
    @Transactional(readOnly = true)
    public List<CrewMember> getUserHistory(Long userId) {
        log.debug("Getting membership history for user: {}", userId);
        return memberRepository.findUserHistory(userId);
    }
    
    /**
     * Verifica si un usuario está en una cuadrilla activa.
     * 
     * @param userId ID del usuario
     * @return true si está en una cuadrilla activa
     */
    @Transactional(readOnly = true)
    public boolean isUserInActiveCrew(Long userId) {
        return memberRepository.isUserInActiveCrew(userId);
    }
    
    /**
     * Cuenta miembros activos de una cuadrilla.
     * 
     * @param crewId ID de la cuadrilla
     * @return Número de miembros activos
     */
    @Transactional(readOnly = true)
    public long countActiveMembers(Long crewId) {
        return memberRepository.countActiveMembers(crewId);
    }
    
    /**
     * Obtiene todos los líderes activos del sistema.
     * 
     * @return Lista de todos los líderes activos
     */
    @Transactional(readOnly = true)
    public List<CrewMember> getAllActiveLeaders() {
        log.debug("Getting all active leaders");
        return memberRepository.findAllActiveLeaders();
    }
}
