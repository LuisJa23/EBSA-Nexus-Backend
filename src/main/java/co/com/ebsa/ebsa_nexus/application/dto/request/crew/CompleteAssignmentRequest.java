package co.com.ebsa.ebsa_nexus.application.dto.request.crew;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for completing an incident assignment
 * Includes optional completion notes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteAssignmentRequest {
    
    /**
     * Optional notes about the completion
     * If provided, must not exceed 1000 characters
     */
    @Size(max = 1000, message = "Completion notes must not exceed 1000 characters")
    private String completionNotes;
}
