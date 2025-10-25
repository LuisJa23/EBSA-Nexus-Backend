package co.com.ebsa.ebsa_nexus.infrastructure.persistence.adapter;

import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.repository.NoveltyRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaNoveltyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa NoveltyRepository del dominio usando JPA.
 * Conecta la capa de dominio con la infraestructura de persistencia.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Component
public class NoveltyRepositoryAdapter implements NoveltyRepository {

    private final JpaNoveltyRepository jpaNoveltyRepository;

    public NoveltyRepositoryAdapter(JpaNoveltyRepository jpaNoveltyRepository) {
        this.jpaNoveltyRepository = jpaNoveltyRepository;
    }

    @Override
    public Novelty save(Novelty novelty) {
        return jpaNoveltyRepository.save(novelty);
    }

    @Override
    public Optional<Novelty> findById(Long id) {
        return jpaNoveltyRepository.findById(id);
    }

    @Override
    public Page<Novelty> findAll(Pageable pageable) {
        return jpaNoveltyRepository.findAll(pageable);
    }

    @Override
    public List<Novelty> findByCrewIdOrderByCreatedAtDesc(Long crewId) {
        return jpaNoveltyRepository.findByCrewIdOrderByCreatedAtDesc(crewId);
    }

    @Override
    public List<Novelty> findByStatusOrderByCreatedAtDesc(NoveltyStatus status) {
        return jpaNoveltyRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public List<Novelty> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jpaNoveltyRepository.findByCreatedAtBetween(startDateTime, endDateTime);
    }

    @Override
    public List<Novelty> findByCrewIdAndCreatedAtBetween(Long crewId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jpaNoveltyRepository.findByCrewIdAndCreatedAtBetween(crewId, startDateTime, endDateTime);
    }

    @Override
    public long countByStatus(NoveltyStatus status) {
        return jpaNoveltyRepository.countByStatus(status);
    }

    @Override
    public Page<Novelty> findByFilters(
            NoveltyStatus status,
            String reason,
            Long crewId,
            Long createdBy,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return jpaNoveltyRepository.findByFilters(
                status, reason, crewId, createdBy, startDate, endDate, pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaNoveltyRepository.existsById(id);
    }

    @Override
    public Page<Novelty> findByStatus(NoveltyStatus status, Pageable pageable) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return jpaNoveltyRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Novelty> findByCreatedById(Long createdById, Pageable pageable) {
        if (createdById == null) {
            throw new IllegalArgumentException("Created by ID cannot be null");
        }
        return jpaNoveltyRepository.findByCreatedByOrderByCreatedAtDesc(createdById, pageable);
    }
}
