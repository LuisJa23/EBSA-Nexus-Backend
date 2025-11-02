package co.com.ebsa.ebsa_nexus.application.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for user performance statistics.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPerformanceResponse {
    private List<UserPerformanceData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPerformanceData {
        private Long userId;
        private String fullName;
        private String workRole;
        private Long noveltiesCreated;
        private Long noveltiesCompleted;
        private Long reportsGenerated;
        private Long participationsInReports;
        private Double averageResolutionTimeHours;
    }
}
