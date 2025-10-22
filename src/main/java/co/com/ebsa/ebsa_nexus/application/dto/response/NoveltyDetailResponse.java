package co.com.ebsa.ebsa_nexus.application.dto.response;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoveltyDetailResponse {
    
    private Long id;
    private Long crewId;
    private NoveltyReason reason;
    private String description;
    private String location;
    private NoveltyStatus status;
    private Long reportedByUserId;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private Long resolvedByUserId;
    private String resolutionNotes;
    private Long verifiedByUserId;
    private String verificationNotes;
    private LocalDateTime verifiedAt;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
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
