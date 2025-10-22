package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for generating statistical reports about novelties.
 * Used by admins to generate reports for specific periods and filters.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalReportRequest {
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    /**
     * Optional filter by crew ID
     */
    private Long crewId;
    
    /**
     * Optional filter by status
     */
    private String status;
    
    /**
     * Type of report to generate
     */
    @NotNull(message = "Report type is required")
    private String reportType;
}
