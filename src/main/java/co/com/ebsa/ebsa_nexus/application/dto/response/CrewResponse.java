package co.com.ebsa.ebsa_nexus.application.dto.response;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for crew response
 * Contains all crew information to be returned to clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewResponse {
    
    /**
     * Unique identifier of the crew
     */
    private Long id;
    
    /**
     * Name of the crew
     */
    private String name;
    
    /**
     * Description of the crew and its purpose
     */
    private String description;
    
    /**
     * Current status of the crew
     */
    private CrewStatus status;
    
    /**
     * ID of the user who created this crew
     */
    private Long createdBy;
    
    /**
     * Timestamp when the crew was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the crew was last updated
     */
    private LocalDateTime updatedAt;
    
    /**
     * Timestamp when the crew was soft-deleted (null if active)
     */
    private LocalDateTime deletedAt;
    
    /**
     * Number of active members in the crew
     */
    private Integer activeMemberCount;
    
    /**
     * Whether the crew is currently assigned to any incident
     */
    private Boolean hasActiveAssignments;
}
