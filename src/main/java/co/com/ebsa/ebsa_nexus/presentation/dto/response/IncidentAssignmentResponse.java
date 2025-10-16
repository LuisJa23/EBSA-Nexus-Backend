package co.com.ebsa.ebsa_nexus.presentation.dto.response;

import co.com.ebsa.ebsa_nexus.domain.crew.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for incident assignment response
 * Contains complete information about an assignment including crew and incident details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentAssignmentResponse {
    
    /**
     * Unique identifier of the assignment
     */
    private Long id;
    
    /**
     * ID of the crew assigned to the incident
     */
    private Long crewId;
    
    /**
     * Name of the assigned crew
     */
    private String crewName;
    
    /**
     * ID of the incident
     */
    private Long incidentId;
    
    /**
     * Title of the incident (if available)
     */
    private String incidentTitle;
    
    /**
     * Current status of the assignment
     */
    private AssignmentStatus status;
    
    /**
     * ID of the user who made the assignment
     */
    private Long assignedBy;
    
    /**
     * Username of who assigned (if available)
     */
    private String assignedByUsername;
    
    /**
     * Timestamp when the assignment was created
     */
    private LocalDateTime assignedAt;
    
    /**
     * Timestamp when work on the assignment started
     */
    private LocalDateTime startedAt;
    
    /**
     * Timestamp when the assignment was completed
     */
    private LocalDateTime completedAt;
    
    /**
     * Timestamp when the assignment was cancelled
     */
    private LocalDateTime cancelledAt;
    
    /**
     * Notes about the assignment (work progress, completion notes, etc.)
     */
    private String notes;
}
