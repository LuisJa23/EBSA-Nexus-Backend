package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for generating a novelty report.
 * Only the crew leader can generate reports.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {
    
    /**
     * Content of the report.
     * Detailed description of actions taken.
     * Must be between 20 and 5000 characters.
     */
    @NotBlank(message = "Report content is required")
    @Size(min = 20, max = 5000, message = "Report content must be between 20 and 5000 characters")
    private String reportContent;
    
    /**
     * Final observations from the crew leader (optional).
     */
    @Size(max = 2000, message = "Observations must not exceed 2000 characters")
    private String observations;
}
