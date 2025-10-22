package co.com.ebsa.ebsa_nexus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa la asignación de una cuadrilla a una novedad.
 * 
 * <p>Mantiene un historial completo de todas las asignaciones,
 * permitiendo rastrear reasignaciones y cambios.</p>
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "novelty_assignments", indexes = {
    @Index(name = "idx_assignments_novelty_id", columnList = "novelty_id"),
    @Index(name = "idx_assignments_crew_id", columnList = "assigned_crew_id"),
    @Index(name = "idx_assignments_assigned_by", columnList = "assigned_by_user_id")
})
public class NoveltyAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "novelty_id", nullable = false)
    private Long noveltyId;
    
    @Column(name = "assigned_crew_id", nullable = false)
    private Long assignedCrewId;
    
    @Column(name = "assigned_by_user_id", nullable = false)
    private Long assignedByUserId;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Column(length = 20)
    private String priority;
    
    @Column(name = "estimated_resolution_date")
    private LocalDate estimatedResolutionDate;
    
    @Column(name = "assigned_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime assignedAt = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoveltyAssignment that = (NoveltyAssignment) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("NoveltyAssignment[id=%d, noveltyId=%d, crewId=%d]", 
            id, noveltyId, assignedCrewId);
    }
}
