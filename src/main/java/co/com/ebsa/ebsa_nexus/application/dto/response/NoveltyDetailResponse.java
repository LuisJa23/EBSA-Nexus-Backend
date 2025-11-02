package co.com.ebsa.ebsa_nexus.application.dto.response;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for novelty detail response.
 * Contains complete information aligned with meter reading form.
 * 
 * @author EBSA Nexus Team
 * @version 2.0
 * @since 2025-10-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoveltyDetailResponse {
    
    private Long id;
    private Long areaId;
    private NoveltyReason reason;
    private String accountNumber;
    private String meterNumber;
    private BigDecimal activeReading;
    private BigDecimal reactiveReading;
    private Long locationId;
    private String locationName;
    private String address;
    private String description;
    private String observations;
    private NoveltyStatus status;
    private Long createdBy;
    private Long crewId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime closedAt;
    private LocalDateTime cancelledAt;
    private List<ImageDetail> images;
    private NoveltyAssignment assignment;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDetail {
        private Long id;
        private String imageUrl;
        private Long uploadedByUserId;
        private LocalDateTime uploadedAt;
    }
}
