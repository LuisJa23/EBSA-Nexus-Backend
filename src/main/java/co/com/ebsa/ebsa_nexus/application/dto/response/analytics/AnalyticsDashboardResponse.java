package co.com.ebsa.ebsa_nexus.application.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for consolidated analytics dashboard.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardResponse {
    private NoveltyOverviewResponse overview;
    private NoveltyTrendResponse trends;
    private List<CrewPerformanceResponse.CrewPerformanceData> topCrews;
    private List<UserPerformanceResponse.UserPerformanceData> topUsers;
    private List<MunicipalityDistributionResponse.MunicipalityData> byMunicipality;
}
