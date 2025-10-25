package co.com.ebsa.ebsa_nexus.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for crew member response
 * Contains information about a crew member and their role
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewMemberResponse {
    
    /**
     * Unique identifier of the crew membership
     */
    private Long id;
    
    /**
     * ID of the crew this member belongs to
     */
    private Long crewId;
    
    /**
     * Name of the crew
     */
    private String crewName;
    
    /**
     * ID of the user who is a member
     */
    private Long userId;
    
    /**
     * Username of the member
     */
    private String username;
    
    /**
     * Full name of the member (if available)
     */
    private String fullName;
    
    /**
     * Whether this member is the leader of the crew
     */
    private Boolean isLeader;
    
    /**
     * Timestamp when the member joined the crew
     */
    private LocalDateTime joinedAt;
    
    /**
     * Timestamp when the member left the crew (null if still active)
     */
    private LocalDateTime leftAt;
    
    /**
     * Timestamp when the membership record was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the membership record was last updated
     */
    private LocalDateTime updatedAt;
}
