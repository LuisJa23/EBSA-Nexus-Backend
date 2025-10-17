package co.com.ebsa.ebsa_nexus.application.dto.request.crew;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a member to a crew
 * Can be used for both regular members and leaders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequest {
    
    /**
     * ID of the user to add to the crew
     * Must be a positive number
     */
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    /**
     * Whether this member should be added as a leader
     * Default is false (regular member)
     */
    @Builder.Default
    private Boolean isLeader = false;
}
