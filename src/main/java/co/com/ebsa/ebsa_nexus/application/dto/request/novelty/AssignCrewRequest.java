package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for assigning a crew to a novelty.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignCrewRequest {
    
    @NotNull(message = "Assigned crew ID is required")
    @Positive(message = "Assigned crew ID must be positive")
    private Long assignedCrewId;
    
    @NotBlank(message = "Instructions are required")
    @Size(max = 2000, message = "Instructions must not exceed 2000 characters")
    private String instructions;
    
    @NotBlank(message = "Priority is required")
    @Size(max = 20, message = "Priority must not exceed 20 characters")
    private String priority;
    
    private LocalDate estimatedResolutionDate;
}
