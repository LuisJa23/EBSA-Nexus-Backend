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
 * Entidad que representa reportes estadísticos periódicos sobre novedades.
 * 
 * <p>Este tipo de reporte agrega información de múltiples novedades
 * en un período de tiempo específico para análisis y toma de decisiones.</p>
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statistical_reports", indexes = {
    @Index(name = "idx_statistical_reports_generated_by", columnList = "generated_by_user_id"),
    @Index(name = "idx_statistical_reports_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_statistical_reports_type", columnList = "report_type")
})
public class StatisticalReport {
    
    /**
     * Identificador único del reporte.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Tipo de reporte (DAILY, WEEKLY, MONTHLY, CUSTOM).
     */
    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;
    
    /**
     * Fecha de inicio del período analizado.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    /**
     * Fecha de fin del período analizado.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    /**
     * ID de la cuadrilla (filtro opcional).
     */
    @Column(name = "crew_id")
    private Long crewId;
    
    /**
     * Estado de novedades (filtro opcional).
     */
    @Column(name = "status", length = 50)
    private String status;
    
    /**
     * Datos del reporte en formato JSON.
     * Contiene estadísticas agregadas, contadores, métricas, etc.
     */
    @Column(name = "report_data", nullable = false, columnDefinition = "TEXT")
    private String reportData;
    
    /**
     * ID del usuario que generó el reporte.
     */
    @Column(name = "generated_by_user_id", nullable = false)
    private Long generatedByUserId;
    
    /**
     * Fecha y hora de generación del reporte.
     */
    @Column(name = "generated_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();
    
    // ========== Lifecycle Callbacks ==========
    
    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
    
    // ========== Business Methods ==========
    
    /**
     * Verifica si el reporte fue generado por un usuario específico.
     * 
     * @param userId ID del usuario a verificar
     * @return true si el usuario generó el reporte, false en caso contrario
     */
    public boolean wasGeneratedBy(Long userId) {
        return Objects.equals(generatedByUserId, userId);
    }
    
    /**
     * Verifica si el reporte es para una cuadrilla específica.
     * 
     * @return true si el reporte filtra por cuadrilla, false si es general
     */
    public boolean isCrewSpecific() {
        return crewId != null;
    }
    
    /**
     * Verifica si el reporte tiene filtro de estado.
     * 
     * @return true si el reporte filtra por estado, false si incluye todos
     */
    public boolean hasStatusFilter() {
        return status != null && !status.trim().isEmpty();
    }
    
    /**
     * Calcula la duración del período analizado en días.
     * 
     * @return Número de días del período
     */
    public long getPeriodDurationDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    // ========== equals y hashCode ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticalReport that = (StatisticalReport) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("StatisticalReport[id=%d, type=%s, period=%s to %s, crew=%s]",
            id,
            reportType,
            startDate,
            endDate,
            crewId != null ? crewId : "ALL");
    }
}
