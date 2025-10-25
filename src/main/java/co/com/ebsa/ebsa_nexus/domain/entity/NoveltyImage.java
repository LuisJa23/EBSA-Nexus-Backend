package co.com.ebsa.ebsa_nexus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa una imagen asociada a una novedad.
 * 
 * <p>Las imágenes sirven como evidencia visual del problema reportado
 * o de la resolución realizada por la cuadrilla.</p>
 * 
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>Máximo 5 imágenes por novedad</li>
 *   <li>Las imágenes se almacenan en Firebase Storage</li>
 *   <li>Solo se guarda la URL en la base de datos</li>
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
@Table(name = "novelty_images", indexes = {
    @Index(name = "idx_novelty_images_novelty_id", columnList = "novelty_id"),
    @Index(name = "idx_novelty_images_uploaded_by", columnList = "uploaded_by_user_id")
})
public class NoveltyImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "novelty_id", nullable = false)
    private Long noveltyId;
    
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
    
    @Column(name = "uploaded_by_user_id", nullable = false)
    private Long uploadedByUserId;
    
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
    
    public boolean hasValidUrl() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoveltyImage that = (NoveltyImage) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("NoveltyImage[id=%d, noveltyId=%d]", id, noveltyId);
    }
}
