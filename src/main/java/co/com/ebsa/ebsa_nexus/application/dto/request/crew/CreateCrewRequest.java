package co.com.ebsa.ebsa_nexus.application.dto.request.crew;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a new crew
 * Validates all required fields for crew creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCrewRequest {
    
    /**
     * Name of the crew
     * Must be between 3 and 100 characters
     */
    @NotBlank(message = "Crew name is required")
    @Size(min = 3, max = 100, message = "Crew name must be between 3 and 100 characters")
    private String name;
    
    /**
     * Description of the crew and its purpose
     * Must be between 10 and 500 characters
     */
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;
    
    /**
     * ID of the user creating this crew
     * Must be a positive number
     */
    @NotNull(message = "Creator ID is required")
    @Positive(message = "Creator ID must be positive")
    private Long createdBy;
    
    /**
     * List of members to add to the crew
     * Must have at least one member and exactly one leader
     */
    @NotEmpty(message = "Crew must have at least one member")
    @Valid
    private List<CreateCrewMemberRequest> members;
}
