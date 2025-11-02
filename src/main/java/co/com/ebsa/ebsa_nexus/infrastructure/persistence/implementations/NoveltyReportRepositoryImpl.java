package co.com.ebsa.ebsa_nexus.infrastructure.persistence.implementations;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyReport;
import co.com.ebsa.ebsa_nexus.domain.repository.NoveltyReportRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaNoveltyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de NoveltyReport.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Component
@RequiredArgsConstructor
public class NoveltyReportRepositoryImpl implements NoveltyReportRepository {
    
    private final JpaNoveltyReportRepository jpaRepository;
    
    @Override
    public NoveltyReport save(NoveltyReport report) {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }
        return jpaRepository.save(report);
    }
    
    @Override
    public Optional<NoveltyReport> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<NoveltyReport> findByNoveltyId(Long noveltyId) {
        return jpaRepository.findByNoveltyId(noveltyId);
    }
    
    @Override
    public List<NoveltyReport> findByGeneratedById(Long userId) {
        return jpaRepository.findByGeneratedById(userId);
    }
    
    @Override
    public boolean existsByNoveltyId(Long noveltyId) {
        return jpaRepository.existsByNoveltyId(noveltyId);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
