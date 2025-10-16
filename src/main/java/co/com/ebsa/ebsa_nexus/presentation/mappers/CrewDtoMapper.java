package co.com.ebsa.ebsa_nexus.presentation.mappers;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.Crew;
import co.com.ebsa.ebsa_nexus.presentation.dto.response.CrewResponse;
import org.springframework.stereotype.Component;

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
}
