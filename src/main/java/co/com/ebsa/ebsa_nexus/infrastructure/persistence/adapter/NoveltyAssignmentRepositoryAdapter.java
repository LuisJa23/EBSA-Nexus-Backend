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
        return jpaRepository.findByNoveltyIdOrderByAssignedAtDesc(noveltyId);
    }

    @Override
    public Optional<NoveltyAssignment> findActiveByNoveltyId(Long noveltyId) {
        if (noveltyId == null) {
            throw new IllegalArgumentException("Novelty ID cannot be null");
        }
        return jpaRepository.findFirstByNoveltyIdAndActiveOrderByAssignedAtDesc(noveltyId, true);
    }

    @Override
    public List<NoveltyAssignment> findByAssignedCrewId(Long crewId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        return jpaRepository.findByAssignedCrewIdOrderByAssignedAtDesc(crewId);
    }

    @Override
    public List<NoveltyAssignment> findByAssignedByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return jpaRepository.findByAssignedByUserIdOrderByAssignedAtDesc(userId);
    }

    @Override
    public long countByAssignedCrewId(Long crewId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        return jpaRepository.countByAssignedCrewId(crewId);
    }

    @Override
    public long countActiveAssignmentsByCrewId(Long crewId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        return jpaRepository.countByAssignedCrewIdAndActive(crewId, true);
    }

    @Override
    public void delete(NoveltyAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("NoveltyAssignment cannot be null");
        }
        jpaRepository.delete(assignment);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return jpaRepository.existsById(id);
    }

    @Override
    public List<NoveltyAssignment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
