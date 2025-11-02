package co.com.ebsa.ebsa_nexus.infrastructure.persistence.implementations;

import co.com.ebsa.ebsa_nexus.domain.entity.ReportParticipant;
import co.com.ebsa.ebsa_nexus.domain.repository.ReportParticipantRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaReportParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementación del repositorio de ReportParticipant.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Component
@RequiredArgsConstructor
public class ReportParticipantRepositoryImpl implements ReportParticipantRepository {
    
    private final JpaReportParticipantRepository jpaRepository;
    
    @Override
    public ReportParticipant save(ReportParticipant participant) {
        return jpaRepository.save(participant);
    }
    
    @Override
    public List<ReportParticipant> findByReportId(Long reportId) {
        return jpaRepository.findByReportId(reportId);
    }
    
    @Override
    public List<ReportParticipant> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }
    
    @Override
    public void deleteByReportId(Long reportId) {
        jpaRepository.deleteByReportId(reportId);
    }
}
