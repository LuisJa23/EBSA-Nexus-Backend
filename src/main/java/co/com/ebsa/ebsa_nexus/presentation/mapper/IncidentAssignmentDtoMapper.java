package co.com.ebsa.ebsa_nexus.presentation.mapper;

import co.com.ebsa.ebsa_nexus.application.dto.response.IncidentAssignmentResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.IncidentAssignment;

import org.springframework.stereotype.Component;

/**
 * Mapper for converting between IncidentAssignment domain entities and DTOs
 * Handles transformation of assignment data for presentation layer
 */
@Component
public class IncidentAssignmentDtoMapper {
    
    /**
     * Converts an IncidentAssignment domain entity to an IncidentAssignmentResponse DTO
     * Basic conversion without additional crew/incident/user information
     * 
     * @param assignment The assignment entity to convert
     * @return IncidentAssignmentResponse DTO with assignment information
     */
    public IncidentAssignmentResponse toResponse(IncidentAssignment assignment) {
        if (assignment == null) {
            return null;
        }
        
        return IncidentAssignmentResponse.builder()
                .id(assignment.getId())
                .crewId(assignment.getCrewId())
                .incidentId(assignment.getIncidentId())
                .status(assignment.getStatus())
                .assignedBy(assignment.getAssignedBy())
                .assignedAt(assignment.getAssignedAt())
                .startedAt(assignment.getStartedAt())
                .completedAt(assignment.getCompletedAt())
                .cancelledAt(assignment.getCancelledAt())
                .notes(assignment.getNotes())
                .build();
    }
    
    /**
     * Converts an IncidentAssignment domain entity to an IncidentAssignmentResponse DTO 
     * with additional information
     * 
     * @param assignment The assignment entity to convert
     * @param crewName Name of the assigned crew
     * @param incidentTitle Title of the incident
     * @param assignedByUsername Username of who made the assignment
     * @return IncidentAssignmentResponse DTO with complete assignment information
     */
    public IncidentAssignmentResponse toResponse(IncidentAssignment assignment, 
                                                  String crewName, 
                                                  String incidentTitle,
                                                  String assignedByUsername) {
        if (assignment == null) {
            return null;
        }
        
        IncidentAssignmentResponse response = toResponse(assignment);
        response.setCrewName(crewName);
        response.setIncidentTitle(incidentTitle);
        response.setAssignedByUsername(assignedByUsername);
        
        return response;
    }
}
