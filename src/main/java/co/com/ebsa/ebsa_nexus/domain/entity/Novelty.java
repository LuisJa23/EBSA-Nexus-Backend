package co.com.ebsa.ebsa_nexus.domain.entity;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad de dominio que representa una novedad del sistema.
 * 
 * <p>Una novedad es un incidente o problema reportado por un supervisor
 * que requiere atención de una cuadrilla de trabajo.</p>
 * 
 * <p><b>Flujo de estados:</b></p>
 * <ul>
 *   <li>REPORTED: Novedad reportada por supervisor</li>
 *   <li>ASSIGNED: Cuadrilla asignada</li>
 *   <li>IN_PROGRESS: Trabajo en proceso</li>
 *   <li>RESOLVED: Trabajo finalizado, pendiente verificación</li>
 *   <li>CLOSED: Verificada y cerrada</li>
 *   <li>CANCELLED: Cancelada por administrador</li>
 * </ul>
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
@Table(name = "novelties", indexes = {
    @Index(name = "idx_novelties_status", columnList = "status"),
    @Index(name = "idx_novelties_crew_id", columnList = "crew_id"),
    @Index(name = "idx_novelties_reason", columnList = "reason"),
    @Index(name = "idx_novelties_reported_by", columnList = "reported_by_user_id"),
    @Index(name = "idx_novelties_reported_at", columnList = "reported_at")
})
public class Novelty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "crew_id", nullable = true)
    private Long crewId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NoveltyStatus status = NoveltyStatus.REPORTED;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NoveltyReason reason;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 255)
    private String location;
    
    @Column(name = "reported_by_user_id", nullable = false)
    private Long reportedByUserId;
    
    @Column(name = "reported_at", nullable = false)
    @Builder.Default
    private LocalDateTime reportedAt = LocalDateTime.now();
    
    @Column(name = "resolved_by_user_id")
    private Long resolvedByUserId;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "verified_by_user_id")
    private Long verifiedByUserId;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;
    
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ========== Lifecycle Callbacks ==========
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (reportedAt == null) {
            reportedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = NoveltyStatus.REPORTED;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // ========== Business Methods ==========
    
    public boolean isCancellable() {
        return status.isCancellable();
    }
    
    public boolean allowsCrewAssignment() {
        return status.allowsCrewAssignment();
    }
    
    public boolean allowsCrewReassignment() {
        return status.allowsCrewReassignment();
    }
    
    public boolean allowsEvidenceUpload() {
        return status.allowsEvidenceUpload();
    }
    
    public boolean allowsReportGeneration() {
        return status.allowsReportGeneration();
    }
    
    public boolean isTerminal() {
        return status.isTerminal();
    }
    
    public void transitionTo(NoveltyStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid transition from %s to %s", status, newStatus)
            );
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Novelty novelty = (Novelty) o;
        return Objects.equals(id, novelty.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Novelty[id=%d, status=%s, reason=%s]", 
            id, status, reason);
    }
}
