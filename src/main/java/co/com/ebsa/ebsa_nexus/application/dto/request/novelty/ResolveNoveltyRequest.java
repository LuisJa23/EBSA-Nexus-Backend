package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for resolving a novelty.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveNoveltyRequest {
    
    @NotBlank(message = "Resolution notes are required")
    private String resolutionNotes;
}
