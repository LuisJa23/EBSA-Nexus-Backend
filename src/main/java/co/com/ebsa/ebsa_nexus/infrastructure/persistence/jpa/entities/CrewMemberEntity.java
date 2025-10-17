package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la membresía de un usuario en una cuadrilla.
 * Mapea a la tabla 'crew_members'.
 * 
 * <p>Esta tabla funciona como una tabla de asociación entre usuarios y cuadrillas,
 * manteniendo el historial completo de membresías.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Entity
@Table(name = "crew_members", 
    indexes = {
        @Index(name = "idx_crew", columnList = "crew_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_active", columnList = "left_at"),
        @Index(name = "idx_leader", columnList = "crew_id, is_leader")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_active_member",
            columnNames = {"crew_id", "user_id", "left_at"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewMemberEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "crew_id", nullable = false)
    private Long crewId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "is_leader", nullable = false)
    private Boolean isLeader;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Callback ejecutado antes de persistir la entidad.
     * Establece las fechas de creación y actualización.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (joinedAt == null) {
            joinedAt = now;
        }
        if (isLeader == null) {
            isLeader = false;
        }
    }
    
    /**
     * Callback ejecutado antes de actualizar la entidad.
     * Actualiza la fecha de modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrewMemberEntity)) return false;
        CrewMemberEntity that = (CrewMemberEntity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "CrewMemberEntity{" +
                "id=" + id +
                ", crewId=" + crewId +
                ", userId=" + userId +
                ", isLeader=" + isLeader +
                ", leftAt=" + leftAt +
                '}';
    }
}
