package co.com.ebsa.ebsa_nexus.infrastructure.persistence.mappers;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.entities.CrewEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Crew (dominio) y CrewEntity (infraestructura).
 * Implementa conversión bidireccional con manejo de nulos.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Component
public class CrewMapper {
    
    /**
     * Convierte una entidad JPA a objeto de dominio.
     * 
     * @param entity Entidad JPA
     * @return Objeto de dominio, null si entity es null
     */
    public Crew toDomain(CrewEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Crew.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(mapStringToStatus(entity.getStatus()))
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
    
    /**
     * Convierte un objeto de dominio a entidad JPA.
     * 
     * @param crew Objeto de dominio
     * @return Entidad JPA, null si crew es null
     */
    public CrewEntity toEntity(Crew crew) {
        if (crew == null) {
            return null;
        }
        
        CrewEntity entity = new CrewEntity();
        entity.setId(crew.getId());
        entity.setName(crew.getName());
        entity.setDescription(crew.getDescription());
        entity.setStatus(mapStatusToString(crew.getStatus()));
        entity.setCreatedBy(crew.getCreatedBy());
        entity.setCreatedAt(crew.getCreatedAt());
        entity.setUpdatedAt(crew.getUpdatedAt());
        entity.setDeletedAt(crew.getDeletedAt());
        
        return entity;
    }
    
    /**
     * Mapea String a CrewStatus.
     * 
     * @param status String del status
     * @return CrewStatus enum, null si status es null
     */
    private CrewStatus mapStringToStatus(String status) {
        if (status == null) {
            return null;
        }
        return CrewStatus.valueOf(status);
    }
    
    /**
     * Mapea CrewStatus a String.
     * 
     * @param status CrewStatus enum
     * @return String del status, null si status es null
     */
    private String mapStatusToString(CrewStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }
}
