package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for analytics queries on novelties.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
public interface AnalyticsRepository {
    
    /**
     * Count total novelties within date range and optional area filter.
     */
    Long countNovelties(LocalDateTime startDate, LocalDateTime endDate, Long areaId);
    
    /**
     * Count novelties grouped by status.
     */
    Map<String, Long> countByStatus(LocalDateTime startDate, LocalDateTime endDate, Long areaId);
    
    /**
     * Count novelties grouped by area.
     */
    Map<String, Long> countByArea(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count novelties grouped by reason.
     */
    Map<String, Long> countByReason(LocalDateTime startDate, LocalDateTime endDate, Long areaId);
    
    /**
     * Calculate average resolution time in hours for completed novelties.
     */
    Double calculateAverageResolutionTime(LocalDateTime startDate, LocalDateTime endDate, Long areaId);
    
    /**
     * Count resolved novelties (COMPLETADA status).
     */
    Long countResolvedNovelties(LocalDateTime startDate, LocalDateTime endDate, Long areaId);
    
    /**
     * Count pending novelties (CREADA, EN_CURSO statuses).
     */
    Long countPendingNovelties(LocalDateTime startDate, LocalDateTime endDate, Long areaId);
    
    /**
     * Get novelty trends grouped by period (daily, weekly, monthly).
     */
    List<Map<String, Object>> getNoveltyTrends(String period, LocalDateTime startDate, LocalDateTime endDate, Long areaId);
    
    /**
     * Get crew performance statistics.
     */
    List<Map<String, Object>> getCrewPerformance(LocalDateTime startDate, LocalDateTime endDate, Long crewId);
    
    /**
     * Get user performance statistics.
     */
    List<Map<String, Object>> getUserPerformance(LocalDateTime startDate, LocalDateTime endDate, Long userId, Long workRoleId);
    
    /**
     * Get novelty distribution by municipality.
     */
    List<Map<String, Object>> getNoveltyByMunicipality(LocalDateTime startDate, LocalDateTime endDate, NoveltyStatus status);
    
    /**
     * Get top performing crews.
     */
    List<Map<String, Object>> getTopCrews(Integer limit, LocalDateTime startDate, LocalDateTime endDate, String sortBy);
    
    /**
     * Get top performing users.
     */
    List<Map<String, Object>> getTopUsers(Integer limit, LocalDateTime startDate, LocalDateTime endDate, String sortBy);
}
