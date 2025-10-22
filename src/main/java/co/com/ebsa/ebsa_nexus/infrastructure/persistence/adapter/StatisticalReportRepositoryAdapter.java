package co.com.ebsa.ebsa_nexus.infrastructure.persistence.adapter;

import co.com.ebsa.ebsa_nexus.domain.entity.StatisticalReport;
import co.com.ebsa.ebsa_nexus.domain.repository.StatisticalReportRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaStatisticalReportRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador que conecta el repositorio de dominio StatisticalReportRepository
 * con el repositorio JPA de Spring Data.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-01-21
 */
@Component
public class StatisticalReportRepositoryAdapter implements StatisticalReportRepository {

    private final JpaStatisticalReportRepository jpaRepository;

    public StatisticalReportRepositoryAdapter(JpaStatisticalReportRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StatisticalReport save(StatisticalReport report) {
        if (report == null) {
            throw new IllegalArgumentException("StatisticalReport cannot be null");
        }
        return jpaRepository.save(report);
    }

    @Override
    public Optional<StatisticalReport> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return jpaRepository.findById(id);
    }

    @Override
    public List<StatisticalReport> findByGeneratedByUserIdOrderByGeneratedAtDesc(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return jpaRepository.findByGeneratedByUserIdOrderByGeneratedAtDesc(userId);
    }

    @Override
    public List<StatisticalReport> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        return jpaRepository.findByStartDateBetween(startDate, endDate);
    }

    @Override
    public List<StatisticalReport> findByReportType(String reportType) {
        if (reportType == null || reportType.trim().isEmpty()) {
            throw new IllegalArgumentException("Report type cannot be null or empty");
        }
        return jpaRepository.findByReportType(reportType);
    }

    @Override
    public List<StatisticalReport> findByCrewId(Long crewId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        return jpaRepository.findByCrewId(crewId);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public long countByGeneratedAtBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        return jpaRepository.countByGeneratedAtBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );
    }
}
