package co.com.ebsa.ebsa_nexus.application.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for crew performance statistics.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewPerformanceResponse {
    private List<CrewPerformanceData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrewPerformanceData {
        private Long crewId;
        private String crewName;
        private Long assignedNovelties;
        private Long completedNovelties;
        private Long pendingNovelties;
        private Double averageResolutionTimeHours;
        private Double completionRate;
        private Integer memberCount;
    }
}
