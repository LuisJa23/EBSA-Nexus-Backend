package co.com.ebsa.ebsa_nexus.application.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for novelty overview statistics.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoveltyOverviewResponse {
    private Long totalNovelties;
    private Map<String, Long> byStatus;
    private Map<String, Long> byArea;
    private Map<String, Long> byReason;
    private Double averageResolutionTimeHours;
    private Long resolvedNovelties;
    private Long pendingNovelties;
}
