package co.com.ebsa.ebsa_nexus.application.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for novelty distribution by municipality.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalityDistributionResponse {
    private List<MunicipalityData> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MunicipalityData {
        private String municipality;
        private Long totalNovelties;
        private Long completed;
        private Long pending;
    }
}
