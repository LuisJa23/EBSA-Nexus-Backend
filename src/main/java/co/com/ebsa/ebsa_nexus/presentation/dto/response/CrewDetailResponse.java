package co.com.ebsa.ebsa_nexus.presentation.dto.response;

import co.com.ebsa.ebsa_nexus.domain.crew.enums.CrewStatus;
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
     * Total number of active members
     */
    private Integer totalMembers;
    
    /**
     * Total number of active assignments
     */
    private Integer totalActiveAssignments;
}
