package co.com.ebsa.ebsa_nexus.application.dto.response;

import co.com.ebsa.ebsa_nexus.domain.enums.ResolutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para un reporte de novedad.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoveltyReportResponse {
    
    private Long id;
    private Long noveltyId;
    private UserSummary generatedBy;
    private String reportContent;
    private String observations;
    private LocalDateTime workStartDate;
    private LocalDateTime workEndDate;
    private ResolutionStatus resolutionStatus;
    private List<ParticipantResponse> participants;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String fullName;
        private String email;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantResponse {
        private Long userId;
        private String fullName;
        private LocalDateTime addedAt;
    }
}
