package co.com.ebsa.ebsa_nexus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidad de dominio que representa la membresía de un usuario en una cuadrilla.
 * 
 * <p>Esta entidad actúa como tabla de asociación entre usuarios y cuadrillas,
 * manteniendo el historial completo de membresías.</p>
 * 
 * <p><b>Reglas de negocio principales:</b></p>
 * <ul>
 *   <li>Un usuario solo puede estar activo en UNA cuadrilla a la vez</li>
 *   <li>Siempre debe haber exactamente 1 jefe por cuadrilla</li>
 *   <li>No se puede quitar al último miembro de una cuadrilla</li>
 *   <li>El historial se mantiene con left_at (no se elimina físicamente)</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Entity
@Table(name = "crew_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewMember {
    
    /**
     * Identificador único de la membresía.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * ID de la cuadrilla a la que pertenece.
     */
    @Column(name = "crew_id", nullable = false)
    private Long crewId;
    
    /**
     * ID del usuario miembro.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * Indica si este miembro es el jefe de la cuadrilla.
     * Solo un miembro puede tener is_leader = true por cuadrilla activa.
     */
    @Column(name = "is_leader", nullable = false)
    private Boolean isLeader;
    
    /**
     * Fecha en que el usuario se unió a la cuadrilla.
     */
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    /**
     * Fecha en que el usuario dejó la cuadrilla.
     * NULL si aún es miembro activo.
     */
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    
    /**
     * Fecha de creación del registro.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Fecha de última actualización.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // ========== Métodos de Negocio ==========
    
    /**
     * Verifica si el miembro está activo en la cuadrilla.
     * 
     * @return true si left_at es null, false en caso contrario
     */
    public boolean isActive() {
        return leftAt == null;
    }
    
    /**
     * Verifica si este miembro es el jefe de la cuadrilla.
     * 
     * @return true si is_leader es true, false en caso contrario
     */
    public boolean isLeader() {
        return Boolean.TRUE.equals(isLeader);
    }
    
    /**
     * Verifica si este miembro es un trabajador regular (no jefe).
     * 
     * @return true si no es líder, false en caso contrario
     */
    public boolean isRegularMember() {
        return !isLeader();
    }
    
    /**
     * Calcula los días que lleva el miembro en la cuadrilla.
     * 
     * @return Número de días desde joined_at hasta ahora (o hasta left_at si ya salió)
     */
    public long getDaysInCrew() {
        LocalDateTime endDate = (leftAt != null) ? leftAt : LocalDateTime.now();
        return ChronoUnit.DAYS.between(joinedAt, endDate);
    }
    
    /**
     * Marca que el miembro dejó la cuadrilla.
     */
    public void markAsLeft() {
        this.leftAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Promueve a este miembro como jefe de la cuadrilla.
     */
    public void promoteToLeader() {
        this.isLeader = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Remueve el rol de jefe de este miembro.
     */
    public void demoteFromLeader() {
        this.isLeader = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // ========== equals y hashCode ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrewMember that = (CrewMember) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "CrewMember{" +
                "id=" + id +
                ", crewId=" + crewId +
                ", userId=" + userId +
                ", isLeader=" + isLeader +
                ", active=" + isActive() +
                '}';
    }
}
