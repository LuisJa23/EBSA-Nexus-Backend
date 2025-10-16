package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.implementations;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.IncidentAssignment;
import co.com.ebsa.ebsa_nexus.domain.crew.enums.AssignmentStatus;
import co.com.ebsa.ebsa_nexus.domain.crew.repositories.IncidentAssignmentRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities.IncidentAssignmentEntity;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.repositories.JpaIncidentAssignmentRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.mappers.IncidentAssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de IncidentAssignment usando Spring Data JPA.
 * Actúa como adaptador entre la capa de dominio y la infraestructura de persistencia.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Repository
@RequiredArgsConstructor
public class IncidentAssignmentRepositoryImpl implements IncidentAssignmentRepository {
    
    private final JpaIncidentAssignmentRepository jpaRepository;
    private final IncidentAssignmentMapper mapper;
    
    @Override
    public IncidentAssignment save(IncidentAssignment assignment) {
        IncidentAssignmentEntity entity = mapper.toEntity(assignment);
        IncidentAssignmentEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<IncidentAssignment> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<IncidentAssignment> findByCrew(Long crewId) {
        return jpaRepository.findByCrewId(crewId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IncidentAssignment> findActiveAssignments(Long crewId) {
        return jpaRepository.findActiveAssignmentsByCrewId(crewId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IncidentAssignment> findByIncident(Long incidentId) {
        return jpaRepository.findByNoveltyId(incidentId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<IncidentAssignment> findActiveAssignment(Long incidentId) {
        return jpaRepository.findActiveAssignmentByNoveltyId(incidentId)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean hasOpenAssignments(Long crewId) {
        return jpaRepository.hasOpenAssignments(crewId);
    }
    
    @Override
    public List<IncidentAssignment> findByStatus(AssignmentStatus status) {
        String statusString = status.name();
        return jpaRepository.findByStatus(statusString).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IncidentAssignment> findCompletedAssignments(Long crewId) {
        return jpaRepository.findCompletedAssignmentsByCrewId(crewId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public int countCompletedAssignments(Long crewId) {
        return jpaRepository.countCompletedAssignmentsByCrewId(crewId);
    }
    
    @Override
    public List<IncidentAssignment> findByAssignedBy(Long userId) {
        return jpaRepository.findByAssignedBy(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IncidentAssignment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByDateRange(startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public int countActiveAssignments(Long crewId) {
        return jpaRepository.countActiveAssignmentsByCrewId(crewId);
    }
}
