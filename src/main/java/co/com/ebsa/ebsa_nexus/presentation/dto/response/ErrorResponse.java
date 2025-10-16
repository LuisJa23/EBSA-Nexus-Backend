package co.com.ebsa.ebsa_nexus.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for API error response
 * Used to return consistent error messages to clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * HTTP status code
     */
    private Integer status;
    
    /**
     * Error message describing what went wrong
     */
    private String message;
    
    /**
     * Additional details about the error (optional)
     */
    private String details;
    
    /**
     * Timestamp when the error occurred
     */
    private String timestamp;
    
    /**
     * Path where the error occurred
     */
    private String path;
}
