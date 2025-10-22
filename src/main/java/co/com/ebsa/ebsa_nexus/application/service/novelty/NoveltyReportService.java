package co.com.ebsa.ebsa_nexus.application.service.novelty;

import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.GenerateReportRequest;
import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyReport;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.novelty.NoveltyOperationException;
import co.com.ebsa.ebsa_nexus.domain.repository.NoveltyReportRepository;
import co.com.ebsa.ebsa_nexus.domain.repository.NoveltyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to generate reports and statistics for novelties
 */
@Service
@Transactional
public class NoveltyReportService {

    private final NoveltyRepository noveltyRepository;
    private final NoveltyReportRepository noveltyReportRepository;

    public NoveltyReportService(
            NoveltyRepository noveltyRepository,
            NoveltyReportRepository noveltyReportRepository) {
        this.noveltyRepository = noveltyRepository;
        this.noveltyReportRepository = noveltyReportRepository;
    }

    /**
     * Generate a comprehensive report for given period and filters
     */
    public NoveltyReport generateReport(GenerateReportRequest request, Long generatedByUserId) {
        // Validate request
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new NoveltyOperationException("Start date must be before end date");
        }

        // Generate report data
        Map<String, Object> reportData = new HashMap<>();
        
        // Get novelties for the period
        List<Novelty> novelties = noveltyRepository.findByReportedAtBetween(
                request.getStartDate().atStartOfDay(),
                request.getEndDate().atTime(23, 59, 59)
        );

        // Filter by crew if specified
        if (request.getCrewId() != null) {
            novelties = novelties.stream()
                    .filter(n -> n.getCrewId().equals(request.getCrewId()))
                    .toList();
        }

        // Filter by status if specified
        if (request.getStatus() != null) {
            NoveltyStatus status = NoveltyStatus.valueOf(request.getStatus());
            novelties = novelties.stream()
                    .filter(n -> n.getStatus().equals(status))
                    .toList();
        }

        // Calculate statistics
        reportData.put("totalNovelties", novelties.size());
        reportData.put("periodStart", request.getStartDate().toString());
        reportData.put("periodEnd", request.getEndDate().toString());
        
        // Count by status
        Map<String, Long> byStatus = new HashMap<>();
        for (NoveltyStatus status : NoveltyStatus.values()) {
            long count = novelties.stream()
                    .filter(n -> n.getStatus().equals(status))
                    .count();
            byStatus.put(status.name(), count);
        }
        reportData.put("byStatus", byStatus);

        // Count by reason
        Map<String, Long> byReason = new HashMap<>();
        for (NoveltyReason reason : NoveltyReason.values()) {
            long count = novelties.stream()
                    .filter(n -> n.getReason().equals(reason))
                    .count();
            byReason.put(reason.name(), count);
        }
        reportData.put("byReason", byReason);

        // Calculate resolution metrics
        List<Novelty> resolvedNovelties = novelties.stream()
                .filter(n -> n.getResolvedAt() != null)
                .toList();

        if (!resolvedNovelties.isEmpty()) {
            // Average resolution time in hours
            double avgResolutionHours = resolvedNovelties.stream()
                    .mapToLong(n -> java.time.Duration.between(n.getReportedAt(), n.getResolvedAt()).toHours())
                    .average()
                    .orElse(0.0);
            reportData.put("averageResolutionTimeHours", avgResolutionHours);

            // Resolution rate
            double resolutionRate = (resolvedNovelties.size() * 100.0) / novelties.size();
            reportData.put("resolutionRate", String.format("%.2f%%", resolutionRate));
        }

        // Count verified vs rejected
        long verifiedCount = novelties.stream()
                .filter(n -> n.getStatus().equals(NoveltyStatus.CLOSED))
                .count();
        long rejectedCount = novelties.stream()
                .filter(n -> n.getStatus().equals(NoveltyStatus.IN_PROGRESS) && n.getVerifiedByUserId() != null)
                .count();
        reportData.put("verifiedCount", verifiedCount);
        reportData.put("rejectedCount", rejectedCount);

        // Top reasons
        String topReason = byReason.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        reportData.put("topReason", topReason);

        // Create report entity
        NoveltyReport report = new NoveltyReport();
        report.setReportType(request.getReportType());
        report.setStartDate(request.getStartDate());
        report.setEndDate(request.getEndDate());
        report.setCrewId(request.getCrewId());
        report.setStatus(request.getStatus());
        report.setReportData(reportData.toString()); // Convert to JSON string in real implementation
        report.setGeneratedByUserId(generatedByUserId);
        report.setGeneratedAt(LocalDateTime.now());

        // Save and return
        return noveltyReportRepository.save(report);
    }

    /**
     * Get statistics summary for dashboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Count by status
        for (NoveltyStatus status : NoveltyStatus.values()) {
            long count = noveltyRepository.countByStatus(status);
            stats.put(status.name().toLowerCase() + "Count", count);
        }

        // Today's novelties
        LocalDate today = LocalDate.now();
        List<Novelty> todayNovelties = noveltyRepository.findByReportedAtBetween(
                today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );
        stats.put("todayCount", todayNovelties.size());

        // This week's novelties
        LocalDate weekStart = today.minusDays(7);
        List<Novelty> weekNovelties = noveltyRepository.findByReportedAtBetween(
                weekStart.atStartOfDay(),
                today.atTime(23, 59, 59)
        );
        stats.put("weekCount", weekNovelties.size());

        // Pending assignment (REPORTED status)
        long pendingAssignment = noveltyRepository.countByStatus(NoveltyStatus.REPORTED);
        stats.put("pendingAssignment", pendingAssignment);

        // Pending verification (RESOLVED status)
        long pendingVerification = noveltyRepository.countByStatus(NoveltyStatus.RESOLVED);
        stats.put("pendingVerification", pendingVerification);

        return stats;
    }

    /**
     * Get crew performance metrics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCrewPerformance(Long crewId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> performance = new HashMap<>();

        // Get crew novelties in period
        List<Novelty> crewNovelties = noveltyRepository.findByCrewIdAndReportedAtBetween(
                crewId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        performance.put("totalNovelties", crewNovelties.size());

        // Resolved count
        long resolvedCount = crewNovelties.stream()
                .filter(n -> n.getStatus().equals(NoveltyStatus.CLOSED))
                .count();
        performance.put("resolvedCount", resolvedCount);

        // Resolution rate
        if (!crewNovelties.isEmpty()) {
            double resolutionRate = (resolvedCount * 100.0) / crewNovelties.size();
            performance.put("resolutionRate", String.format("%.2f%%", resolutionRate));
        }

        // Average resolution time
        List<Novelty> resolvedNovelties = crewNovelties.stream()
                .filter(n -> n.getResolvedAt() != null)
                .toList();

        if (!resolvedNovelties.isEmpty()) {
            double avgHours = resolvedNovelties.stream()
                    .mapToLong(n -> java.time.Duration.between(n.getReportedAt(), n.getResolvedAt()).toHours())
                    .average()
                    .orElse(0.0);
            performance.put("averageResolutionHours", avgHours);
        }

        return performance;
    }

    /**
     * Get report by ID
     */
    @Transactional(readOnly = true)
    public NoveltyReport getReportById(Long reportId) {
        return noveltyReportRepository.findById(reportId)
                .orElseThrow(() -> new NoveltyOperationException("Report not found with id: " + reportId));
    }

    /**
     * Get all reports generated by user
     */
    @Transactional(readOnly = true)
    public List<NoveltyReport> getReportsByUser(Long userId) {
        return noveltyReportRepository.findByGeneratedByUserIdOrderByGeneratedAtDesc(userId);
    }
}
