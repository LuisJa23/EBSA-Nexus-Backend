package co.com.ebsa.ebsa_nexus.application.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for novelty trends over time.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoveltyTrendResponse {
    private String period;
    private List<TrendDataPoint> trends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataPoint {
        private String period;
        private Long created;
        private Long completed;
        private Long cancelled;
    }
}
