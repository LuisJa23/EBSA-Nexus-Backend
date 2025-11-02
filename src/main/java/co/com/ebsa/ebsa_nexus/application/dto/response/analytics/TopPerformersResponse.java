package co.com.ebsa.ebsa_nexus.application.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for top performers (users or crews).
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopPerformersResponse {
    private String type;
    private List<PerformerData> topPerformers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformerData {
        private Long id;
        private String name;
        private Long completedNovelties;
        private Double completionRate;
        private Double averageResolutionTimeHours;
    }
}
