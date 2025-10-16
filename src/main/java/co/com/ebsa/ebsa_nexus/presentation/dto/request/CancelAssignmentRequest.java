package co.com.ebsa.ebsa_nexus.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for canceling an incident assignment
 * Requires a reason for the cancellation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelAssignmentRequest {
    
    /**
     * Reason for canceling the assignment
     * Must be provided and between 10 and 500 characters
     */
    @NotBlank(message = "Cancellation reason is required")
    @Size(min = 10, max = 500, message = "Cancellation reason must be between 10 and 500 characters")
    private String reason;
}
