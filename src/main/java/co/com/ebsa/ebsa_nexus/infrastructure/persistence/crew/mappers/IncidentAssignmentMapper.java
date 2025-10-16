package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.mappers;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.IncidentAssignment;
import co.com.ebsa.ebsa_nexus.domain.crew.enums.AssignmentStatus;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities.IncidentAssignmentEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre IncidentAssignment (dominio) y IncidentAssignmentEntity (infraestructura).
 * Implementa conversión bidireccional con manejo de nulos.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Component
public class IncidentAssignmentMapper {
    
    /**
     * Convierte una entidad JPA a objeto de dominio.
     * 
     * @param entity Entidad JPA
     * @return Objeto de dominio, null si entity es null
     */
    public IncidentAssignment toDomain(IncidentAssignmentEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return IncidentAssignment.builder()
                .id(entity.getId())
                .crewId(entity.getCrewId())
                .incidentId(entity.getNoveltyId())
                .assignedBy(entity.getAssignedBy())
                .status(mapStringToStatus(entity.getStatus()))
                .assignedAt(entity.getAssignedAt())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .cancelledAt(entity.getCancelledAt())
                .notes(entity.getNotes())
                .build();
    }
    
    /**
     * Convierte un objeto de dominio a entidad JPA.
     * 
     * @param assignment Objeto de dominio
     * @return Entidad JPA, null si assignment es null
     */
    public IncidentAssignmentEntity toEntity(IncidentAssignment assignment) {
        if (assignment == null) {
            return null;
        }
        
        IncidentAssignmentEntity entity = new IncidentAssignmentEntity();
        entity.setId(assignment.getId());
        entity.setCrewId(assignment.getCrewId());
        entity.setNoveltyId(assignment.getIncidentId());
        entity.setAssignedBy(assignment.getAssignedBy());
        entity.setStatus(mapStatusToString(assignment.getStatus()));
        entity.setAssignedAt(assignment.getAssignedAt());
        entity.setStartedAt(assignment.getStartedAt());
        entity.setCompletedAt(assignment.getCompletedAt());
        entity.setCancelledAt(assignment.getCancelledAt());
        entity.setNotes(assignment.getNotes());
        
        return entity;
    }
    
    /**
     * Mapea String a AssignmentStatus.
     * 
     * @param status String del status
     * @return AssignmentStatus enum, null si status es null
     */
    private AssignmentStatus mapStringToStatus(String status) {
        if (status == null) {
            return null;
        }
        return AssignmentStatus.valueOf(status);
    }
    
    /**
     * Mapea AssignmentStatus a String.
     * 
     * @param status AssignmentStatus enum
     * @return String del status, null si status es null
     */
    private String mapStatusToString(AssignmentStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }
}
