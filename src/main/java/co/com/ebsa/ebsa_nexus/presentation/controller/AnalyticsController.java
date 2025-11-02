package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.response.ApiResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.analytics.*;
import co.com.ebsa.ebsa_nexus.application.service.analytics.AnalyticsApplicationService;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller for Analytics and Statistics.
 * Provides endpoints for novelty statistics, performance metrics, and dashboards.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsApplicationService analyticsService;

    /**
     * Get general novelty overview statistics.
     * 
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param areaId Optional area filter
     * @return Novelty overview statistics
     */
    @GetMapping("/novelties/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'AREA_MANAGER')")
    public ResponseEntity<ApiResponse<NoveltyOverviewResponse>> getNoveltyOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long areaId) {
        
        log.info("GET /api/v1/analytics/novelties/overview - StartDate: {}, EndDate: {}, AreaId: {}", 
            startDate, endDate, areaId);
        
        NoveltyOverviewResponse response = analyticsService.getNoveltyOverview(startDate, endDate, areaId);
        
        return ResponseEntity.ok(ApiResponse.<NoveltyOverviewResponse>builder()
            .success(true)
            .data(response)
            .build());
    }

    /**
     * Get novelty trends over time.
     * 
     * @param period Time period: daily, weekly, monthly (default: monthly)
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param areaId Optional area filter
     * @return Novelty trends by period
     */
    @GetMapping("/novelties/trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'AREA_MANAGER')")
    public ResponseEntity<ApiResponse<NoveltyTrendResponse>> getNoveltyTrends(
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long areaId) {
        
        log.info("GET /api/v1/analytics/novelties/trends - Period: {}, StartDate: {}, EndDate: {}", 
            period, startDate, endDate);
        
        NoveltyTrendResponse response = analyticsService.getNoveltyTrends(period, startDate, endDate, areaId);
        
        return ResponseEntity.ok(ApiResponse.<NoveltyTrendResponse>builder()
            .success(true)
            .data(response)
            .build());
    }

    /**
     * Get crew performance statistics.
     * 
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param crewId Optional crew ID to filter specific crew
     * @return Crew performance metrics
     */
    @GetMapping("/crews/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'AREA_MANAGER')")
    public ResponseEntity<ApiResponse<CrewPerformanceResponse>> getCrewPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long crewId) {
        
        log.info("GET /api/v1/analytics/crews/performance - StartDate: {}, EndDate: {}, CrewId: {}", 
            startDate, endDate, crewId);
        
        CrewPerformanceResponse response = analyticsService.getCrewPerformance(startDate, endDate, crewId);
        
        return ResponseEntity.ok(ApiResponse.<CrewPerformanceResponse>builder()
            .success(true)
            .data(response)
            .build());
    }

    /**
     * Get user performance statistics.
     * 
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param userId Optional user ID to filter specific user
     * @param workRoleId Optional work role ID to filter by role
     * @return User performance metrics
     */
    @GetMapping("/users/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'AREA_MANAGER')")
    public ResponseEntity<ApiResponse<UserPerformanceResponse>> getUserPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long workRoleId) {
        
        log.info("GET /api/v1/analytics/users/performance - StartDate: {}, EndDate: {}, UserId: {}, WorkRoleId: {}", 
            startDate, endDate, userId, workRoleId);
        
        UserPerformanceResponse response = analyticsService.getUserPerformance(startDate, endDate, userId, workRoleId);
        
        return ResponseEntity.ok(ApiResponse.<UserPerformanceResponse>builder()
            .success(true)
            .data(response)
            .build());
    }

    /**
     * Get novelty distribution by municipality.
     * 
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param status Optional status filter
     * @return Novelty distribution by municipality
     */
    @GetMapping("/novelties/by-municipality")
    @PreAuthorize("hasAnyRole('ADMIN', 'AREA_MANAGER')")
    public ResponseEntity<ApiResponse<MunicipalityDistributionResponse>> getNoveltyByMunicipality(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) NoveltyStatus status) {
        
        log.info("GET /api/v1/analytics/novelties/by-municipality - StartDate: {}, EndDate: {}, Status: {}", 
            startDate, endDate, status);
        
        MunicipalityDistributionResponse response = analyticsService.getNoveltyByMunicipality(startDate, endDate, status);
        
        return ResponseEntity.ok(ApiResponse.<MunicipalityDistributionResponse>builder()
            .success(true)
            .data(response)
            .build());
    }

    /**
     * Get top performers (users or crews).
     * 
     * @param type Type of performers: users or crews
     * @param limit Maximum number of results (default: 10)
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param sortBy Sort criteria: completionRate, totalCompleted, averageTime (default: completionRate)
     * @return Top performers list
     */
    @GetMapping("/top-performers")
    @PreAuthorize("hasAnyRole('ADMIN', 'AREA_MANAGER')")
    public ResponseEntity<ApiResponse<TopPerformersResponse>> getTopPerformers(
            @RequestParam(defaultValue = "users") String type,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "completionRate") String sortBy) {
        
        log.info("GET /api/v1/analytics/top-performers - Type: {}, Limit: {}, SortBy: {}", 
            type, limit, sortBy);
        
        TopPerformersResponse response = analyticsService.getTopPerformers(type, limit, startDate, endDate, sortBy);
        
        return ResponseEntity.ok(ApiResponse.<TopPerformersResponse>builder()
            .success(true)
            .data(response)
            .build());
    }

    /**
     * Get consolidated analytics dashboard with all main metrics.
     * 
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @return Consolidated dashboard data
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'AREA_MANAGER')")
    public ResponseEntity<ApiResponse<AnalyticsDashboardResponse>> getAnalyticsDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("GET /api/v1/analytics/dashboard - StartDate: {}, EndDate: {}", startDate, endDate);
        
        AnalyticsDashboardResponse response = analyticsService.getAnalyticsDashboard(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.<AnalyticsDashboardResponse>builder()
            .success(true)
            .data(response)
            .build());
    }
}
