package co.com.ebsa.ebsa_nexus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa el reporte de resolución de una novedad.
 * 
 * <p>El reporte es generado por el jefe de cuadrilla al completar
 * el trabajo sobre una novedad.</p>
 * 
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>Solo puede haber un reporte por novedad (relación 1:1)</li>
 *   <li>Solo el jefe de cuadrilla puede generar el reporte</li>
 *   <li>Al generar el reporte, la novedad pasa a COMPLETED y luego a CLOSED</li>
 *   <li>El reporte incluye tiempo de resolución calculado</li>
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
@Table(name = "novelty_reports", indexes = {
    @Index(name = "idx_novelty_reports_generated_by", columnList = "generated_by")
})
public class NoveltyReport {
    
    /**
     * Identificador único del reporte.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Novedad asociada al reporte.
     * Relación uno a uno.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novelty_id", nullable = false, unique = true)
    private Novelty novelty;
    
    /**
     * Usuario que generó el reporte.
     * Debe ser el jefe de la cuadrilla asignada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", nullable = false)
    private User generatedBy;
    
    /**
     * Contenido del reporte.
     * Descripción detallada de las acciones realizadas.
     */
    @Column(name = "report_content", nullable = false, columnDefinition = "TEXT")
    private String reportContent;
    
    /**
     * Tiempo de resolución en horas.
     * Calculado automáticamente desde la asignación hasta el reporte.
     */
    @Column(name = "resolution_time_hours")
    private Integer resolutionTimeHours;
    
    /**
     * Observaciones finales del jefe de cuadrilla.
     */
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    /**
     * Fecha de creación del reporte.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // ========== Lifecycle Callbacks ==========
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // ========== Business Methods ==========
    
    /**
     * Verifica si el reporte tiene contenido válido.
     * 
     * @return true si el contenido no es nulo ni vacío, false en caso contrario
     */
    public boolean hasValidContent() {
        return reportContent != null && !reportContent.trim().isEmpty();
    }
    
    /**
     * Verifica si el reporte fue generado por un usuario específico.
     * 
     * @param userId ID del usuario a verificar
     * @return true si el usuario generó el reporte, false en caso contrario
     */
    public boolean wasGeneratedBy(Long userId) {
        return generatedBy != null && Objects.equals(generatedBy.getId(), userId);
    }
    
    /**
     * Calcula el tiempo de resolución en horas.
     * 
     * @param assignmentDateTime Fecha/hora de la asignación inicial
     * @return Tiempo de resolución en horas
     */
    public static int calculateResolutionTime(LocalDateTime assignmentDateTime, LocalDateTime completionDateTime) {
        if (assignmentDateTime == null || completionDateTime == null) {
            return 0;
        }
        
        long hours = java.time.Duration.between(assignmentDateTime, completionDateTime).toHours();
        return (int) hours;
    }
    
    /**
     * Agrega observaciones al reporte.
     * 
     * @param observations Observaciones a agregar
     */
    public void addObservations(String observations) {
        this.observations = observations;
    }
    
    // ========== equals y hashCode ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoveltyReport that = (NoveltyReport) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("NoveltyReport[id=%d, noveltyId=%d, resolutionTime=%dh]",
            id,
            novelty != null ? novelty.getId() : null,
            resolutionTimeHours);
    }
}
