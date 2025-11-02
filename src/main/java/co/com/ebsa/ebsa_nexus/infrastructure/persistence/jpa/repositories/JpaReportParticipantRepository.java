package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.domain.entity.ReportParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para ReportParticipant.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Repository
public interface JpaReportParticipantRepository extends JpaRepository<ReportParticipant, Long> {
    
    @Query("SELECT rp FROM ReportParticipant rp WHERE rp.report.id = :reportId")
    List<ReportParticipant> findByReportId(@Param("reportId") Long reportId);
    
    @Query("SELECT rp FROM ReportParticipant rp WHERE rp.user.id = :userId ORDER BY rp.addedAt DESC")
    List<ReportParticipant> findByUserId(@Param("userId") Long userId);
    
    void deleteByReportId(Long reportId);
}
