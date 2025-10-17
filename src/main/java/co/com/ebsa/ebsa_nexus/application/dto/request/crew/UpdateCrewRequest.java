package co.com.ebsa.ebsa_nexus.application.dto.request.crew;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating crew information
 * All fields are optional - only provided fields will be updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCrewRequest {
    
    /**
     * New name for the crew (optional)
     * If provided, must be between 3 and 100 characters
     */
    @Size(min = 3, max = 100, message = "Crew name must be between 3 and 100 characters")
    private String name;
    
    /**
     * New description for the crew (optional)
     * If provided, must be between 10 and 500 characters
     */
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;
}
