package co.com.ebsa.ebsa_nexus.presentation.mapper;

import co.com.ebsa.ebsa_nexus.application.dto.response.CrewMemberResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;

import org.springframework.stereotype.Component;

/**
 * Mapper for converting between CrewMember domain entities and DTOs
 * Handles transformation of crew member data for presentation layer
 */
@Component
public class CrewMemberDtoMapper {
    
    /**
     * Converts a CrewMember domain entity to a CrewMemberResponse DTO
     * Basic conversion without additional user/crew information
     * 
     * @param member The crew member entity to convert
     * @return CrewMemberResponse DTO with member information
     */
    public CrewMemberResponse toResponse(CrewMember member) {
        if (member == null) {
            return null;
        }
        
        return CrewMemberResponse.builder()
                .id(member.getId())
                .crewId(member.getCrewId())
                .userId(member.getUserId())
                .isLeader(member.getIsLeader())
                .joinedAt(member.getJoinedAt())
                .leftAt(member.getLeftAt())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
    
    /**
     * Converts a CrewMember domain entity to a CrewMemberResponse DTO with additional information
     * 
     * @param member The crew member entity to convert
     * @param crewName Name of the crew
     * @param username Username of the member
     * @param fullName Full name of the member
     * @return CrewMemberResponse DTO with complete member information
     */
    public CrewMemberResponse toResponse(CrewMember member, String crewName, String username, String fullName) {
        if (member == null) {
            return null;
        }
        
        CrewMemberResponse response = toResponse(member);
        response.setCrewName(crewName);
        response.setUsername(username);
        response.setFullName(fullName);
        
        return response;
    }
}
