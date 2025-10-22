package co.com.ebsa.ebsa_nexus.domain.entity;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad de dominio que representa una novedad del sistema.
 * Mapea exactamente los campos del formulario de creación.
 * 
 * <p><b>Flujo de estados:</b></p>
 * <ul>
 *   <li>CREADA: Novedad creada, sin cuadrilla asignada</li>
 *   <li>EN_CURSO: Cuadrilla asignada, trabajo en progreso</li>
 *   <li>COMPLETADA: Trabajo completado</li>
 *   <li>CERRADA: Novedad cerrada</li>
 *   <li>CANCELADA: Novedad cancelada</li>
 * </ul>
 * 
 * @author EBSA Nexus Team
 * @version 2.0
 * @since 2025-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "novelties", indexes = {
    @Index(name = "idx_novelties_status", columnList = "status"),
    @Index(name = "idx_novelties_area_id", columnList = "area_id"),
    @Index(name = "idx_novelties_account", columnList = "account_number"),
    @Index(name = "idx_novelties_meter", columnList = "meter_number"),
    @Index(name = "idx_novelties_municipality", columnList = "municipality")
})
public class Novelty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Campos obligatorios del formulario
    @Column(name = "area_id", nullable = false)
    private Long areaId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NoveltyReason reason;
    
    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;
    
    @Column(name = "meter_number", nullable = false, length = 50)
    private String meterNumber;
    
    @Column(name = "active_reading", nullable = false, precision = 10, scale = 2)
    private BigDecimal activeReading;
    
    @Column(name = "reactive_reading", nullable = false, precision = 10, scale = 2)
    private BigDecimal reactiveReading;
    
    @Column(nullable = false, length = 100)
    private String municipality;
    
    @Column(length = 255)
    private String address;
    
    @Column(nullable = false, length = 255)
    private String location;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    // Control de estado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NoveltyStatus status = NoveltyStatus.CREADA;
    
    @Column(name = "reported_by_user_id", nullable = false)
    private Long createdBy;
    
    // Campo adicional requerido por la BD (mismo valor que createdBy)
    @Column(name = "created_by", nullable = false)
    private Long createdByLegacy;
    
    @Column(name = "crew_id")
    private Long crewId;
    
    // Timestamps
    @Column(name = "reported_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    // ========== Lifecycle Callbacks ==========
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = NoveltyStatus.CREADA;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // ========== Business Methods ==========
    
    /**
     * Asigna una cuadrilla a la novedad
     */
    public void assignCrew(Long crewId) {
        if (!status.canAssignCrew()) {
            throw new IllegalStateException(
                String.format("No se puede asignar cuadrilla en estado: %s", status)
            );
        }
        this.crewId = crewId;
        this.status = NoveltyStatus.EN_CURSO;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marca la novedad como completada
     */
    public void markAsCompleted() {
        if (!status.canComplete()) {
            throw new IllegalStateException(
                String.format("No se puede completar novedad en estado: %s", status)
            );
        }
        this.status = NoveltyStatus.COMPLETADA;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cierra la novedad (estado final)
     */
    public void close() {
        if (!status.canClose()) {
            throw new IllegalStateException(
                String.format("No se puede cerrar novedad en estado: %s", status)
            );
        }
        this.status = NoveltyStatus.CERRADA;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cancela la novedad
     */
    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new IllegalStateException(
                String.format("No se puede cancelar novedad en estado: %s. Solo desde CREADA", status)
            );
        }
        this.status = NoveltyStatus.CANCELADA;
        this.cancelledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Verifica si la novedad tiene cuadrilla asignada
     */
    public boolean hasAssignedCrew() {
        return crewId != null;
    }
    
    public boolean canAssignCrew() {
        return status.canAssignCrew();
    }
    
    public boolean canUploadEvidence() {
        return status.canUploadEvidence();
    }
    
    public boolean canComplete() {
        return status.canComplete();
    }
    
    public boolean canClose() {
        return status.canClose();
    }
    
    public boolean canBeCancelled() {
        return status.canBeCancelled();
    }
    
    public boolean isTerminal() {
        return status.isTerminal();
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
        return String.format("Novelty[id=%d, status=%s, reason=%s, account=%s]", 
            id, status, reason, accountNumber);
    }
}
