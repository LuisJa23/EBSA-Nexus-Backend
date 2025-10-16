package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.mappers;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.CrewMember;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities.CrewMemberEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre CrewMember (dominio) y CrewMemberEntity (infraestructura).
 * Implementa conversión bidireccional con manejo de nulos.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Component
public class CrewMemberMapper {
    
    /**
     * Convierte una entidad JPA a objeto de dominio.
     * 
     * @param entity Entidad JPA
     * @return Objeto de dominio, null si entity es null
     */
    public CrewMember toDomain(CrewMemberEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return CrewMember.builder()
                .id(entity.getId())
                .crewId(entity.getCrewId())
                .userId(entity.getUserId())
                .isLeader(entity.getIsLeader())
                .joinedAt(entity.getJoinedAt())
                .leftAt(entity.getLeftAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Convierte un objeto de dominio a entidad JPA.
     * 
     * @param member Objeto de dominio
     * @return Entidad JPA, null si member es null
     */
    public CrewMemberEntity toEntity(CrewMember member) {
        if (member == null) {
            return null;
        }
        
        CrewMemberEntity entity = new CrewMemberEntity();
        entity.setId(member.getId());
        entity.setCrewId(member.getCrewId());
        entity.setUserId(member.getUserId());
        entity.setIsLeader(member.getIsLeader());
        entity.setJoinedAt(member.getJoinedAt());
        entity.setLeftAt(member.getLeftAt());
        entity.setCreatedAt(member.getCreatedAt());
        entity.setUpdatedAt(member.getUpdatedAt());
        
        return entity;
    }
}
