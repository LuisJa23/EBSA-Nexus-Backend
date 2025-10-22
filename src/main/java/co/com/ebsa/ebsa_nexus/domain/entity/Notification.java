package co.com.ebsa.ebsa_nexus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una notificación del sistema.
 * 
 * <p>Las notificaciones se generan automáticamente para eventos importantes:</p>
 * <ul>
 *   <li>Creación de novedad</li>
 *   <li>Asignación de cuadrilla</li>
 *   <li>Cambio de estado</li>
 *   <li>Completado de novedad</li>
 *   <li>Cierre de novedad</li>
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
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_user_id", columnList = "user_id"),
    @Index(name = "idx_notifications_is_read", columnList = "is_read"),
    @Index(name = "idx_notifications_created_at", columnList = "created_at"),
    @Index(name = "idx_notifications_novelty_id", columnList = "novelty_id")
})
public class Notification {
    
    /**
     * Identificador único de la notificación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Usuario destinatario de la notificación.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Novedad relacionada (opcional).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novelty_id")
    private Novelty novelty;
    
    /**
     * Tipo de notificación.
     * Ejemplos: NOVELTY_CREATED, CREW_ASSIGNED, STATUS_CHANGED, etc.
     */
    @Column(nullable = false, length = 50)
    private String type;
    
    /**
     * Título de la notificación.
     */
    @Column(nullable = false, length = 200)
    private String title;
    
    /**
     * Mensaje descriptivo de la notificación.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    /**
     * Indica si la notificación ha sido leída.
     */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
    
    /**
     * Fecha de creación de la notificación.
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
        if (isRead == null) {
            isRead = false;
        }
    }
    
    // ========== Business Methods ==========
    
    /**
     * Marca la notificación como leída.
     */
    public void markAsRead() {
        this.isRead = true;
    }
    
    /**
     * Marca la notificación como no leída.
     */
    public void markAsUnread() {
        this.isRead = false;
    }
    
    /**
     * Verifica si la notificación ha sido leída.
     * 
     * @return true si está leída, false en caso contrario
     */
    public boolean hasBeenRead() {
        return Boolean.TRUE.equals(isRead);
    }
    
    /**
     * Verifica si la notificación está asociada a una novedad.
     * 
     * @return true si tiene novedad asociada, false en caso contrario
     */
    public boolean hasAssociatedNovelty() {
        return novelty != null;
    }
    
    // ========== equals y hashCode ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Notification[id=%d, type=%s, isRead=%s, userId=%d]",
            id, type, isRead,
            user != null ? user.getId() : null);
    }
}
