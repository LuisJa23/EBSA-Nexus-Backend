package co.com.ebsa.ebsa_nexus.application.dto.response;package co.com.ebsa.ebsa_nexus.application.dto.response;package co.com.ebsa.ebsa_nexus.application.dto.response;



import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;

import lombok.AllArgsConstructor;

import lombok.Builder;import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;

import lombok.Data;

import lombok.NoArgsConstructor;import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;



import java.time.LocalDateTime;import lombok.AllArgsConstructor;import lombok.AllArgsConstructor;

import java.util.List;

import lombok.Builder;import lombok.Builder;

@Data

@Builderimport lombok.Data;import lombok.Data;

@NoArgsConstructor

@AllArgsConstructorimport lombok.NoArgsConstructor;import lombok.NoArgsConstructor;

public class NoveltyDetailResponse {

    

    private Long id;

    private Long crewId;import java.time.LocalDateTime;import java.time.LocalDateTime;

    private NoveltyReason reason;

    private String description;import java.util.List;import java.util.List;

    private String location;

    private NoveltyStatus status;

    private Long reportedByUserId;

    private LocalDateTime reportedAt;/**/**

    private LocalDateTime resolvedAt;

    private Long resolvedByUserId; * DTO for detailed novelty response. * DTO for detailed novelty response.

    private String resolutionNotes;

    private Long verifiedByUserId; * Contains complete information including images, assignments, and reports. * Contains complete information including images, assignments, and reports.

    private String verificationNotes;

    private LocalDateTime verifiedAt; *  * 

    private String cancellationReason;

    private LocalDateTime createdAt; * @author EBSA Nexus Team * @author EBSA Nexus Team

    private LocalDateTime updatedAt;

    private List<ImageDetail> images; * @version 1.0 * @version 1.0

    private NoveltyAssignment assignment;

     * @since 2025-10-21 * @since 2025-10-21

    @Data

    @Builder */ */

    @NoArgsConstructor

    @AllArgsConstructor@Data@Data

    public static class ImageDetail {

        private Long id;@Builder@Builder

        private String imageUrl;

        private Long uploadedByUserId;@NoArgsConstructor@NoArgsConstructor

        private LocalDateTime uploadedAt;

    }@AllArgsConstructor@AllArgsConstructor

}

public class NoveltyDetailResponse {public class NoveltyDetailResponse {

        

    private Long id;    private Long id;

    private Long crewId;    private Long crewId;

    private NoveltyReason reason;    private NoveltyReason reason;

    private String description;    private String description;

    private String location;    private String location;

    private NoveltyStatus status;    private NoveltyStatus status;

    private Long reportedByUserId;    private Long reportedByUserId;

    private LocalDateTime reportedAt;    private LocalDateTime reportedAt;

    private LocalDateTime resolvedAt;    private LocalDateTime resolvedAt;

    private Long resolvedByUserId;    private Long resolvedByUserId;

    private String resolutionNotes;    private String resolutionNotes;

    private Long verifiedByUserId;    private Long verifiedByUserId;

    private String verificationNotes;    private String verificationNotes;

    private LocalDateTime verifiedAt;    private LocalDateTime verifiedAt;

    private String cancellationReason;    private String cancellationReason;

    private LocalDateTime createdAt;    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;    private LocalDateTime updatedAt;

        

    /**    private List<ImageDetail> images;

     * List of images.    private NoveltyAssignment assignment;

     */    

    private List<ImageDetail> images;    @Data

        @Builder

    /**    @NoArgsConstructor

     * Current active assignment (from NoveltyAssignment entity).    @AllArgsConstructor

     */    public static class ImageDetail {

    private NoveltyAssignment assignment;        private Long id;

            private String imageUrl;

    // ========== Nested DTOs ==========        private Long uploadedByUserId;

            private LocalDateTime uploadedAt;

    @Data    }

    @Builder}

    @NoArgsConstructor    private UserDetail createdBy;

    @AllArgsConstructor    

    public static class ImageDetail {    /**

        private Long id;     * Creation timestamp.

        private String imageUrl;     */

        private Long uploadedByUserId;    private LocalDateTime createdAt;

        private LocalDateTime uploadedAt;    

    }    /**

}     * Last update timestamp.

     */
    private LocalDateTime updatedAt;
    
    /**
     * Completion timestamp.
     */
    private LocalDateTime completedAt;
    
    /**
     * Closure timestamp.
     */
    private LocalDateTime closedAt;
    
    /**
     * Cancellation timestamp.
     */
    private LocalDateTime cancelledAt;
    
    /**
     * List of images.
     */
    private List<ImageDetail> images;
    
    /**
     * Current active assignment (from NoveltyAssignment entity).
     */
    private NoveltyAssignment assignment;
    
    /**
     * Report (if exists).
     */
    private ReportDetail report;
    
    // ========== Nested DTOs ==========
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaDetail {
        private Long id;
        private String code;
        private String name;
        private String description;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDetail {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDetail {
        private Long id;
        private String imageUrl;
        private UserDetail uploadedBy;
        private LocalDateTime uploadedAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentDetail {
        private Long id;
        private CrewDetail crew;
        private UserDetail assignedBy;
        private Boolean isActive;
        private String notes;
        private LocalDateTime assignedAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrewDetail {
        private Long id;
        private String name;
        private String description;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDetail {
        private Long id;
        private String reportContent;
        private Integer resolutionTimeHours;
        private String observations;
        private UserDetail generatedBy;
        private LocalDateTime createdAt;
    }
}
