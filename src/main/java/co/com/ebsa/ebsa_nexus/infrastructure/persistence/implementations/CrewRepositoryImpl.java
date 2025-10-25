package co.com.ebsa.ebsa_nexus.infrastructure.persistence.implementations;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.entities.CrewEntity;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaCrewRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.mappers.CrewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Crew usando Spring Data JPA.
 * Actúa como adaptador entre la capa de dominio y la infraestructura de persistencia.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Repository
@RequiredArgsConstructor
public class CrewRepositoryImpl implements CrewRepository {
    
    private final JpaCrewRepository jpaRepository;
    private final CrewMapper mapper;
    
    @Override
    public Crew save(Crew crew) {
        CrewEntity entity = mapper.toEntity(crew);
        CrewEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Crew> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Crew> findActiveById(Long id) {
        return jpaRepository.findActiveById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Crew> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Crew> findByStatus(CrewStatus status) {
        String statusString = status.name();
        return jpaRepository.findByStatusAndActive(statusString).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Crew> findByCreatedBy(Long userId) {
        return jpaRepository.findByCreatedBy(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Crew> findAvailableCrews() {
        return jpaRepository.findAvailableCrews().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public long countActiveCrews() {
        return jpaRepository.countActiveCrews();
    }
    
    @Override
    public long countByStatus(CrewStatus status) {
        String statusString = status.name();
        return jpaRepository.countByStatusAndActive(statusString);
    }
}
