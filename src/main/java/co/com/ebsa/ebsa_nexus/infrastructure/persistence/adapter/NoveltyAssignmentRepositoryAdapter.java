package co.com.ebsa.ebsa_nexus.infrastructure.persistence.adapter;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import co.com.ebsa.ebsa_nexus.domain.repository.NoveltyAssignmentRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaNoveltyAssignmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation for NoveltyAssignmentRepository.
 * Connects domain repository interface to JPA infrastructure.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Component
public class NoveltyAssignmentRepositoryAdapter implements NoveltyAssignmentRepository {
    
    private final JpaNoveltyAssignmentRepository jpaRepository;

    public NoveltyAssignmentRepositoryAdapter(JpaNoveltyAssignmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NoveltyAssignment save(NoveltyAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("NoveltyAssignment cannot be null");
        }
        return jpaRepository.save(assignment);
    }

    @Override
    public Optional<NoveltyAssignment> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return jpaRepository.findById(id);
    }

    @Override
    public List<NoveltyAssignment> findByNoveltyIdOrderByAssignedAtDesc(Long noveltyId) {
        if (noveltyId == null) {
            throw new IllegalArgumentException("Novelty ID cannot be null");
        }
        return jpaRepository.findByNoveltyId(noveltyId);
    }

    @Override
    public Optional<NoveltyAssignment> findLatestByNoveltyId(Long noveltyId) {
        if (noveltyId == null) {
            throw new IllegalArgumentException("Novelty ID cannot be null");
        }
        return jpaRepository.findLatestByNoveltyId(noveltyId);
    }

    @Override
    public List<NoveltyAssignment> findByCrewId(Long crewId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        return jpaRepository.findByCrewId(crewId);
    }

    @Override
    public List<NoveltyAssignment> findByAssignedByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return jpaRepository.findByAssignedByUserId(userId);
    }

    @Override
    public long countByCrewId(Long crewId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        return jpaRepository.countByCrewId(crewId);
    }
}
