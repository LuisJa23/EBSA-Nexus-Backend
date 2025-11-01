package co.com.ebsa.ebsa_nexus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un participante en la resolución de una novedad.
 * 
 * <p>Permite registrar qué miembros específicos de la cuadrilla
 * participaron activamente en la resolución, ya que no siempre
 * todos los miembros asignados trabajan en la novedad.</p>
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_participants", 
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_report_user", columnNames = {"report_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_report_participants_report_id", columnList = "report_id"),
        @Index(name = "idx_report_participants_user_id", columnList = "user_id")
    }
)
public class ReportParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reporte al que pertenece este participante.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private NoveltyReport report;
    
    /**
     * Usuario que participó en la resolución.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Fecha en que se agregó al participante al reporte.
     */
    @Column(name = "added_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime addedAt = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }
}
