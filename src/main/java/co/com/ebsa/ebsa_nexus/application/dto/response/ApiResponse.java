package co.com.ebsa.ebsa_nexus.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for API success response
 * Used to return consistent success messages to clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Whether the operation was successful
     */
    private Boolean success;
    
    /**
     * Success or informational message
     */
    private String message;
    
    /**
     * The actual data payload (generic type)
     */
    private T data;
    
    /**
     * Timestamp of the response
     */
    private String timestamp;
    
    /**
     * Creates a success response with data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
    
    /**
     * Creates a success response without data
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }
}
