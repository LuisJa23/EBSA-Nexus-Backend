package co.com.ebsa.ebsa_nexus.application.factories;

import org.springframework.stereotype.Component;

import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;

import java.time.LocalDateTime;

/**
 * Factory para crear instancias de CrewMember con validaciones de reglas de negocio.
 * 
 * <p>Garantiza que todas las membresías se crean con:
 * <ul>
 *   <li>Timestamps correctos</li>
 *   <li>Estado inicial activo (leftAt = null)</li>
 *   <li>Validación de roles (líder/miembro regular)</li>
 *   <li>Validaciones de campos obligatorios</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Component
public class CrewMemberFactory {
    
    /**
     * Crea un nuevo miembro de cuadrilla (no líder).
     * 
     * @param crewId ID de la cuadrilla (obligatorio)
     * @param userId ID del usuario (obligatorio)
     * @return Nueva instancia de CrewMember
     * @throws IllegalArgumentException si algún campo obligatorio es inválido
     */
    public CrewMember createMember(Long crewId, Long userId) {
        validateMemberCreation(crewId, userId);
        
        LocalDateTime now = LocalDateTime.now();
        
        return CrewMember.builder()
                .crewId(crewId)
                .userId(userId)
                .isLeader(false)
                .joinedAt(now)
                .leftAt(null)
                .build();
    }
    
    /**
     * Crea un nuevo líder de cuadrilla.
     * 
     * @param crewId ID de la cuadrilla (obligatorio)
     * @param userId ID del usuario (obligatorio)
     * @return Nueva instancia de CrewMember como líder
     * @throws IllegalArgumentException si algún campo obligatorio es inválido
     */
    public CrewMember createLeader(Long crewId, Long userId) {
        validateMemberCreation(crewId, userId);
        
        LocalDateTime now = LocalDateTime.now();
        
        return CrewMember.builder()
                .crewId(crewId)
                .userId(userId)
                .isLeader(true)
                .joinedAt(now)
                .leftAt(null)
                .build();
    }
    
    /**
     * Crea un miembro con fecha de ingreso personalizada.
     * Útil para migraciones o registros históricos.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario
     * @param isLeader Si es líder o no
     * @param joinedAt Fecha de ingreso
     * @return Nueva instancia de CrewMember
     * @throws IllegalArgumentException si algún campo obligatorio es inválido
     */
    public CrewMember createMemberWithJoinDate(Long crewId, Long userId, Boolean isLeader, LocalDateTime joinedAt) {
        validateMemberCreation(crewId, userId);
        
        if (joinedAt == null) {
            throw new IllegalArgumentException("Joined at date cannot be null");
        }
        
        if (joinedAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Joined at date cannot be in the future");
        }
        
       
        
        return CrewMember.builder()
                .crewId(crewId)
                .userId(userId)
                .isLeader(isLeader != null ? isLeader : false)
                .joinedAt(joinedAt)
                .leftAt(null)
                .build();
    }
    
    /**
     * Valida los campos obligatorios para crear un miembro.
     * 
     * @param crewId ID de la cuadrilla
     * @param userId ID del usuario
     * @throws IllegalArgumentException si alguna validación falla
     */
    private void validateMemberCreation(Long crewId, Long userId) {
        if (crewId == null) {
            throw new IllegalArgumentException("Crew ID cannot be null");
        }
        
        if (crewId <= 0) {
            throw new IllegalArgumentException("Crew ID must be positive");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }
}
