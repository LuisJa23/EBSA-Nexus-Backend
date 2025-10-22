package co.com.ebsa.ebsa_nexus.application.dto.response;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for novelty summary response.
 * Contains basic information for list views aligned with meter reading form.
 * 
 * @author EBSA Nexus Team
 * @version 2.0
 * @since 2025-10-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoveltyResponse {
    
    private Long id;
    private Long areaId;
    private NoveltyReason reason;
    private String accountNumber;
    private String meterNumber;
    private BigDecimal activeReading;
    private BigDecimal reactiveReading;
    private String municipality;
    private String address;
    private String description;
    private String observations;
    private NoveltyStatus status;
    private Long createdBy;
    private Long crewId;
    
    /**
     * Creation timestamp.
     */
    private LocalDateTime createdAt;
    
    /**
     * Last update timestamp.
     */
    private LocalDateTime updatedAt;
    
    /**
     * Completion timestamp (if applicable).
     */
    private LocalDateTime completedAt;
    
    /**
     * Closure timestamp (if applicable).
     */
    private LocalDateTime closedAt;
    
    /**
     * Cancellation timestamp (if applicable).
     */
    private LocalDateTime cancelledAt;
    
    /**
     * Number of images attached.
     */
    private Integer imageCount;
    
    /**
     * Indicates if this novelty has an assignment.
     */
    private Boolean hasAssignment;
    
    /**
     * Currently assigned crew (if any).
     */
    private CrewSummary assignedCrew;
    
    /**
     * Area summary nested DTO.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaSummary {
        private Long id;
        private String code;
        private String name;
    }
    
    /**
     * User summary nested DTO.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
    }
    
    /**
     * Crew summary nested DTO.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrewSummary {
        private Long id;
        private String name;
    }
}
