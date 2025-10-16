package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.implementations;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.CrewMember;
import co.com.ebsa.ebsa_nexus.domain.crew.repositories.CrewMemberRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities.CrewMemberEntity;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.repositories.JpaCrewMemberRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.mappers.CrewMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de CrewMember usando Spring Data JPA.
 * Actúa como adaptador entre la capa de dominio y la infraestructura de persistencia.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Repository
@RequiredArgsConstructor
public class CrewMemberRepositoryImpl implements CrewMemberRepository {
    
    private final JpaCrewMemberRepository jpaRepository;
    private final CrewMemberMapper mapper;
    
    @Override
    public CrewMember save(CrewMember member) {
        CrewMemberEntity entity = mapper.toEntity(member);
        CrewMemberEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<CrewMember> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<CrewMember> findActiveMembers(Long crewId) {
        return jpaRepository.findActiveMembersByCrewId(crewId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CrewMember> findAllMembers(Long crewId) {
        return jpaRepository.findAllMembersByCrewId(crewId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<CrewMember> findLeader(Long crewId) {
        return jpaRepository.findLeaderByCrewId(crewId)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean isUserInActiveCrew(Long userId) {
        return jpaRepository.existsActiveMembershipByUserId(userId);
    }
    
    @Override
    public Optional<CrewMember> findActiveMembership(Long userId) {
        return jpaRepository.findActiveMembershipByUserId(userId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<CrewMember> findActiveMembership(Long crewId, Long userId) {
        return jpaRepository.findActiveMembership(crewId, userId)
                .map(mapper::toDomain);
    }
    
    @Override
    public int countActiveMembers(Long crewId) {
        return jpaRepository.countActiveMembersByCrewId(crewId);
    }
    
    @Override
    public int countActiveLeaders(Long crewId) {
        return jpaRepository.countActiveLeadersByCrewId(crewId);
    }
    
    @Override
    public List<CrewMember> findUserHistory(Long userId) {
        return jpaRepository.findUserHistory(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CrewMember> findAllActiveLeaders() {
        return jpaRepository.findAllActiveLeaders().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
