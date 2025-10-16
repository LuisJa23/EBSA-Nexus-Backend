package co.com.ebsa.ebsa_nexus.infrastructure.persistence.crew.jpa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una cuadrilla en la base de datos.
 * Mapea a la tabla 'crews'.
 * 
 * <p>Esta clase pertenece a la capa de infraestructura y contiene
 * todas las anotaciones específicas de JPA/Hibernate.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Entity
@Table(name = "crews", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_deleted", columnList = "deleted_at"),
    @Index(name = "fk_crews_users_idx", columnList = "created_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
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
        if (status == null) {
            status = "DISPONIBLE";
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
        if (!(o instanceof CrewEntity)) return false;
        CrewEntity that = (CrewEntity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "CrewEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
