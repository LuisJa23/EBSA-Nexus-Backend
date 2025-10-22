package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new novelty.
 * Validates all required fields for novelty creation.
 * The crew assignment is optional at creation time and can be assigned later.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoveltyRequest {
    
    @NotNull(message = "Reason is required")
    private NoveltyReason reason;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;
    
    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
}
