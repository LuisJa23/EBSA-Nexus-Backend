package co.com.ebsa.ebsa_nexus.application.service.analytics;

import co.com.ebsa.ebsa_nexus.application.dto.response.analytics.*;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application service for analytics and statistics.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsApplicationService {

    private final AnalyticsRepository analyticsRepository;

    /**
     * Get general novelty overview statistics.
     */
    @Transactional(readOnly = true)
    public NoveltyOverviewResponse getNoveltyOverview(LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        log.info("Getting novelty overview. StartDate: {}, EndDate: {}, AreaId: {}", startDate, endDate, areaId);
        
        Long totalNovelties = analyticsRepository.countNovelties(startDate, endDate, areaId);
        Map<String, Long> byStatus = analyticsRepository.countByStatus(startDate, endDate, areaId);
        Map<String, Long> byArea = analyticsRepository.countByArea(startDate, endDate);
        Map<String, Long> byReason = analyticsRepository.countByReason(startDate, endDate, areaId);
        Double avgResolutionTime = analyticsRepository.calculateAverageResolutionTime(startDate, endDate, areaId);
        Long resolvedNovelties = analyticsRepository.countResolvedNovelties(startDate, endDate, areaId);
        Long pendingNovelties = analyticsRepository.countPendingNovelties(startDate, endDate, areaId);
        
        return NoveltyOverviewResponse.builder()
            .totalNovelties(totalNovelties)
            .byStatus(byStatus)
            .byArea(byArea)
            .byReason(byReason)
            .averageResolutionTimeHours(avgResolutionTime)
            .resolvedNovelties(resolvedNovelties)
            .pendingNovelties(pendingNovelties)
            .build();
    }

    /**
     * Get novelty trends over time.
     */
    @Transactional(readOnly = true)
    public NoveltyTrendResponse getNoveltyTrends(String period, LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        log.info("Getting novelty trends. Period: {}, StartDate: {}, EndDate: {}", period, startDate, endDate);
        
        List<Map<String, Object>> rawTrends = analyticsRepository.getNoveltyTrends(period, startDate, endDate, areaId);
        
        List<NoveltyTrendResponse.TrendDataPoint> trends = rawTrends.stream()
            .map(row -> NoveltyTrendResponse.TrendDataPoint.builder()
                .period(row.get("period").toString())
                .created((Long) row.get("created"))
                .completed((Long) row.get("completed"))
                .cancelled((Long) row.get("cancelled"))
                .build())
            .collect(Collectors.toList());
        
        return NoveltyTrendResponse.builder()
            .period(period)
            .trends(trends)
            .build();
    }

    /**
     * Get crew performance statistics.
     */
    @Transactional(readOnly = true)
    public CrewPerformanceResponse getCrewPerformance(LocalDateTime startDate, LocalDateTime endDate, Long crewId) {
        log.info("Getting crew performance. StartDate: {}, EndDate: {}, CrewId: {}", startDate, endDate, crewId);
        
        List<Map<String, Object>> rawData = analyticsRepository.getCrewPerformance(startDate, endDate, crewId);
        
        List<CrewPerformanceResponse.CrewPerformanceData> data = rawData.stream()
            .map(row -> CrewPerformanceResponse.CrewPerformanceData.builder()
                .crewId((Long) row.get("crewId"))
                .crewName((String) row.get("crewName"))
                .assignedNovelties((Long) row.get("assignedNovelties"))
                .completedNovelties((Long) row.get("completedNovelties"))
                .pendingNovelties((Long) row.get("pendingNovelties"))
                .averageResolutionTimeHours((Double) row.get("averageResolutionTimeHours"))
                .completionRate((Double) row.get("completionRate"))
                .memberCount((Integer) row.get("memberCount"))
                .build())
            .collect(Collectors.toList());
        
        return CrewPerformanceResponse.builder()
            .data(data)
            .build();
    }

    /**
     * Get user performance statistics.
     */
    @Transactional(readOnly = true)
    public UserPerformanceResponse getUserPerformance(LocalDateTime startDate, LocalDateTime endDate, Long userId, Long workRoleId) {
        log.info("Getting user performance. StartDate: {}, EndDate: {}, UserId: {}, WorkRoleId: {}", 
            startDate, endDate, userId, workRoleId);
        
        List<Map<String, Object>> rawData = analyticsRepository.getUserPerformance(startDate, endDate, userId, workRoleId);
        
        List<UserPerformanceResponse.UserPerformanceData> data = rawData.stream()
            .map(row -> UserPerformanceResponse.UserPerformanceData.builder()
                .userId((Long) row.get("userId"))
                .fullName((String) row.get("fullName"))
                .workRole((String) row.get("workRole"))
                .noveltiesCreated((Long) row.get("noveltiesCreated"))
                .noveltiesCompleted((Long) row.get("noveltiesCompleted"))
                .reportsGenerated((Long) row.get("reportsGenerated"))
                .participationsInReports((Long) row.get("participationsInReports"))
                .averageResolutionTimeHours((Double) row.get("averageResolutionTimeHours"))
                .build())
            .collect(Collectors.toList());
        
        return UserPerformanceResponse.builder()
            .data(data)
            .build();
    }

    /**
     * Get novelty distribution by municipality.
     */
    @Transactional(readOnly = true)
    public MunicipalityDistributionResponse getNoveltyByMunicipality(LocalDateTime startDate, LocalDateTime endDate, NoveltyStatus status) {
        log.info("Getting novelty distribution by municipality. StartDate: {}, EndDate: {}, Status: {}", 
            startDate, endDate, status);
        
        List<Map<String, Object>> rawData = analyticsRepository.getNoveltyByMunicipality(startDate, endDate, status);
        
        List<MunicipalityDistributionResponse.MunicipalityData> data = rawData.stream()
            .map(row -> MunicipalityDistributionResponse.MunicipalityData.builder()
                .municipality((String) row.get("municipality"))
                .totalNovelties((Long) row.get("totalNovelties"))
                .completed((Long) row.get("completed"))
                .pending((Long) row.get("pending"))
                .build())
            .collect(Collectors.toList());
        
        return MunicipalityDistributionResponse.builder()
            .data(data)
            .build();
    }

    /**
     * Get top performers (users or crews).
     */
    @Transactional(readOnly = true)
    public TopPerformersResponse getTopPerformers(String type, Integer limit, LocalDateTime startDate, 
                                                   LocalDateTime endDate, String sortBy) {
        log.info("Getting top performers. Type: {}, Limit: {}, SortBy: {}", type, limit, sortBy);
        
        List<Map<String, Object>> rawData;
        
        if ("crews".equalsIgnoreCase(type)) {
            rawData = analyticsRepository.getTopCrews(limit, startDate, endDate, sortBy);
        } else {
            rawData = analyticsRepository.getTopUsers(limit, startDate, endDate, sortBy);
        }
        
        List<TopPerformersResponse.PerformerData> performers = rawData.stream()
            .map(row -> TopPerformersResponse.PerformerData.builder()
                .id((Long) row.get("id"))
                .name((String) row.get("name"))
                .completedNovelties((Long) row.get("completedNovelties"))
                .completionRate((Double) row.get("completionRate"))
                .averageResolutionTimeHours((Double) row.get("averageResolutionTimeHours"))
                .build())
            .collect(Collectors.toList());
        
        return TopPerformersResponse.builder()
            .type(type)
            .topPerformers(performers)
            .build();
    }

    /**
     * Get consolidated analytics dashboard.
     */
    @Transactional(readOnly = true)
    public AnalyticsDashboardResponse getAnalyticsDashboard(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting analytics dashboard. StartDate: {}, EndDate: {}", startDate, endDate);
        
        // If no dates provided, use last 6 months
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(6);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        NoveltyOverviewResponse overview = getNoveltyOverview(startDate, endDate, null);
        NoveltyTrendResponse trends = getNoveltyTrends("monthly", startDate, endDate, null);
        
        List<CrewPerformanceResponse.CrewPerformanceData> topCrews = 
            analyticsRepository.getTopCrews(5, startDate, endDate, "completionRate").stream()
                .map(row -> CrewPerformanceResponse.CrewPerformanceData.builder()
                    .crewId((Long) row.get("id"))
                    .crewName((String) row.get("name"))
                    .completedNovelties((Long) row.get("completedNovelties"))
                    .completionRate((Double) row.get("completionRate"))
                    .averageResolutionTimeHours((Double) row.get("averageResolutionTimeHours"))
                    .build())
                .collect(Collectors.toList());
        
        List<UserPerformanceResponse.UserPerformanceData> topUsers = 
            analyticsRepository.getTopUsers(5, startDate, endDate, "completionRate").stream()
                .map(row -> UserPerformanceResponse.UserPerformanceData.builder()
                    .userId((Long) row.get("id"))
                    .fullName((String) row.get("name"))
                    .noveltiesCompleted((Long) row.get("completedNovelties"))
                    .averageResolutionTimeHours((Double) row.get("averageResolutionTimeHours"))
                    .build())
                .collect(Collectors.toList());
        
        List<MunicipalityDistributionResponse.MunicipalityData> byMunicipality = 
            analyticsRepository.getNoveltyByMunicipality(startDate, endDate, null).stream()
                .map(row -> MunicipalityDistributionResponse.MunicipalityData.builder()
                    .municipality((String) row.get("municipality"))
                    .totalNovelties((Long) row.get("totalNovelties"))
                    .completed((Long) row.get("completed"))
                    .pending((Long) row.get("pending"))
                    .build())
                .collect(Collectors.toList());
        
        return AnalyticsDashboardResponse.builder()
            .overview(overview)
            .trends(trends)
            .topCrews(topCrews)
            .topUsers(topUsers)
            .byMunicipality(byMunicipality)
            .build();
    }
}
