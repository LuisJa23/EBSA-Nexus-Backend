package co.com.ebsa.ebsa_nexus.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding notes to an assignment
 * Used to append additional information to existing notes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddNotesRequest {
    
    /**
     * Notes to add to the assignment
     * Must be provided and between 5 and 1000 characters
     */
    @NotBlank(message = "Notes are required")
    @Size(min = 5, max = 1000, message = "Notes must be between 5 and 1000 characters")
    private String notes;
}
