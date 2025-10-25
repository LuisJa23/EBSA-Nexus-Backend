package co.com.ebsa.ebsa_nexus.application.dto.request.crew;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning an incident to a crew
 * Includes optional initial notes about the assignment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignIncidentRequest {
    
    /**
     * ID of the incident to assign
     * Must be a positive number
     */
    @NotNull(message = "Incident ID is required")
    @Positive(message = "Incident ID must be positive")
    private Long incidentId;
    
    /**
     * ID of the crew to assign the incident to
     * Must be a positive number
     */
    @NotNull(message = "Crew ID is required")
    @Positive(message = "Crew ID must be positive")
    private Long crewId;
    
    /**
     * ID of the user making the assignment
     * Must be a positive number
     */
    @NotNull(message = "Assigned by ID is required")
    @Positive(message = "Assigned by ID must be positive")
    private Long assignedBy;
    
    /**
     * Optional initial notes about the assignment
     * If provided, must not exceed 1000 characters
     */
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
