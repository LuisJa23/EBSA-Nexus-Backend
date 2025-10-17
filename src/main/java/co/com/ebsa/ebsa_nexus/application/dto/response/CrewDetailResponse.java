package co.com.ebsa.ebsa_nexus.application.dto.response;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for detailed crew response
 * Includes crew information along with its members and active assignments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewDetailResponse {
    
    /**
     * Basic crew information
     */
    private Long id;
    private String name;
    private String description;
    private CrewStatus status;
    private Long createdBy;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private java.time.LocalDateTime deletedAt;
    
    /**
     * List of active members in the crew
     */
    private List<CrewMemberResponse> members;
    
    /**
     * ID of the crew leader (if exists)
     */
    private Long leaderId;
    
    /**
     * Username of the crew leader (if exists)
     */
    private String leaderUsername;
    
    /**
     * List of active assignments for this crew
     */
    private List<IncidentAssignmentResponse> activeAssignments;
    
    /**
     * Number of active members in the crew
     */
    private Integer activeMemberCount;
    
    /**
     * Whether the crew has active assignments
     */
    private Boolean hasActiveAssignments;
    
    /**
     * Total number of active members
     */
    private Integer totalMembers;
    
    /**
     * Total number of active assignments
     */
    private Integer totalActiveAssignments;
}
