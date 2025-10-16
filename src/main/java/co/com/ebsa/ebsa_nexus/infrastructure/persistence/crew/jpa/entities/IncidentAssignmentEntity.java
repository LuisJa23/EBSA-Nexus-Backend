package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la asignación de un incidente/novedad a una cuadrilla.
 * Mapea a la tabla 'novelty_assignments'.
 * 
 * <p>Esta entidad vincula cuadrillas con incidentes y rastrea todo el ciclo de vida
 * de la asignación desde su creación hasta su finalización o cancelación.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Entity
@Table(name = "novelty_assignments",
    indexes = {
        @Index(name = "fk_assignments_crew_idx", columnList = "crew_id"),
        @Index(name = "fk_assignments_novelty_idx", columnList = "novelty_id"),
        @Index(name = "fk_assignments_assigned_by_idx", columnList = "assigned_by"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentAssignmentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "crew_id", nullable = false)
    private Long crewId;
    
    @Column(name = "novelty_id", nullable = false)
    private Long noveltyId;
    
    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;
    
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * Callback ejecutado antes de persistir la entidad.
     * Establece valores por defecto.
     */
    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "ASIGNADO";
        }
    }
    
    /**
     * Callback ejecutado antes de actualizar la entidad.
     * Puede incluir validaciones adicionales si es necesario.
     */
    @PreUpdate
    protected void onUpdate() {
        // Validaciones adicionales pueden ir aquí si es necesario
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IncidentAssignmentEntity)) return false;
        IncidentAssignmentEntity that = (IncidentAssignmentEntity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "IncidentAssignmentEntity{" +
                "id=" + id +
                ", crewId=" + crewId +
                ", noveltyId=" + noveltyId +
                ", status='" + status + '\'' +
                ", assignedAt=" + assignedAt +
                '}';
    }
}
