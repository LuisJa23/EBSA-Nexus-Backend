package co.com.ebsa.ebsa_nexus.application.dto.request.crew;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a crew member within a crew creation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCrewMemberRequest {
    
    /**
     * ID of the user to add as member
     * Must be a positive number
     */
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    /**
     * Whether this member should be the leader of the crew
     * Only one member can be leader per crew
     */
    @NotNull(message = "Leader flag is required")
    private Boolean isLeader;
}