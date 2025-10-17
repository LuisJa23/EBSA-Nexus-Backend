package co.com.ebsa.ebsa_nexus.presentation.mapper;

import co.com.ebsa.ebsa_nexus.application.dto.response.CrewDetailResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.CrewMemberResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.CrewResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting between Crew domain entities and DTOs
 * Handles transformation of crew data for presentation layer
 */
@Component
public class CrewDtoMapper {
    
    /**
     * Converts a Crew domain entity to a CrewResponse DTO
     * 
     * @param crew The crew entity to convert
     * @return CrewResponse DTO with crew information
     */
    public CrewResponse toResponse(Crew crew) {
        if (crew == null) {
            return null;
        }
        
        return CrewResponse.builder()
                .id(crew.getId())
                .name(crew.getName())
                .description(crew.getDescription())
                .status(crew.getStatus())
                .createdBy(crew.getCreatedBy())
                .createdAt(crew.getCreatedAt())
                .updatedAt(crew.getUpdatedAt())
                .deletedAt(crew.getDeletedAt())
                .build();
    }
    
    /**
     * Converts a Crew domain entity to a CrewResponse DTO with additional information
     * 
     * @param crew The crew entity to convert
     * @param activeMemberCount Number of active members
     * @param hasActiveAssignments Whether the crew has active assignments
     * @return CrewResponse DTO with crew information and statistics
     */
    public CrewResponse toResponse(Crew crew, Integer activeMemberCount, Boolean hasActiveAssignments) {
        if (crew == null) {
            return null;
        }
        
        CrewResponse response = toResponse(crew);
        response.setActiveMemberCount(activeMemberCount);
        response.setHasActiveAssignments(hasActiveAssignments);
        
        return response;
    }

    /**
     * Converts a Crew domain entity to a CrewDetailResponse DTO with members information
     * 
     * @param crew The crew entity to convert
     * @param members List of crew members
     * @param leaderId ID of the crew leader (if exists)
     * @param leaderUsername Username of the crew leader (if exists)
     * @param activeMemberCount Number of active members
     * @param hasActiveAssignments Whether the crew has active assignments
     * @return CrewDetailResponse DTO with detailed crew information including members
     */
    public CrewDetailResponse toDetailResponse(Crew crew, List<CrewMemberResponse> members, Long leaderId, String leaderUsername, Integer activeMemberCount, Boolean hasActiveAssignments) {
        if (crew == null) {
            return null;
        }
        
        return CrewDetailResponse.builder()
                .id(crew.getId())
                .name(crew.getName())
                .description(crew.getDescription())
                .status(crew.getStatus())
                .createdBy(crew.getCreatedBy())
                .createdAt(crew.getCreatedAt())
                .updatedAt(crew.getUpdatedAt())
                .deletedAt(crew.getDeletedAt())
                .members(members)
                .leaderId(leaderId)
                .leaderUsername(leaderUsername)
                .activeMemberCount(activeMemberCount)
                .hasActiveAssignments(hasActiveAssignments)
                .totalMembers(members != null ? members.size() : 0)
                .totalActiveAssignments(0) // Se puede calcular más adelante si es necesario
                .build();
    }
}
