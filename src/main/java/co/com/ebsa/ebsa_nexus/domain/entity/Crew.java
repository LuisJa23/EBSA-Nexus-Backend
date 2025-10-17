package co.com.ebsa.ebsa_nexus.domain.entity;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad de dominio que representa una cuadrilla de trabajo.
 * 
 * <p>Una cuadrilla es un equipo dinámico de trabajadores que atiende novedades/incidentes.
 * Las cuadrillas se crean según necesidad y pueden modificarse cuando están disponibles.</p>
 * 
 * <p><b>Reglas de negocio principales:</b></p>
 * <ul>
 *   <li>Debe tener entre 1 y 10 miembros activos</li>
 *   <li>Debe tener exactamente 1 jefe de cuadrilla</li>
 *   <li>Solo se puede modificar en estado DISPONIBLE</li>
 *   <li>La eliminación es lógica (deleted_at != null)</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Crew {
    
    /**
     * Identificador único de la cuadrilla.
     */
    private Long id;
    
    /**
     * Nombre descriptivo de la cuadrilla.
     * Obligatorio, entre 3 y 100 caracteres.
     */
    private String name;
    
    /**
     * Descripción opcional de la cuadrilla.
     * Máximo 255 caracteres.
     */
    private String description;
    
    /**
     * ID del usuario que creó la cuadrilla.
     * Debe ser ADMIN o JEFE_AREA.
     */
    private Long createdBy;
    
    /**
     * Estado actual de la cuadrilla.
     * Por defecto DISPONIBLE.
     */
    private CrewStatus status;
    
    /**
     * Fecha de eliminación lógica.
     * NULL si la cuadrilla está activa.
     */
    private LocalDateTime deletedAt;
    
    /**
     * Fecha de creación del registro.
     */
    private LocalDateTime createdAt;
    
    /**
     * Fecha de última actualización.
     */
    private LocalDateTime updatedAt;
    
    // ========== Métodos de Negocio ==========
    
    /**
     * Verifica si la cuadrilla está activa (no eliminada).
     * 
     * @return true si deleted_at es null, false en caso contrario
     */
    public boolean isActive() {
        return deletedAt == null;
    }
    
    /**
     * Verifica si la cuadrilla está disponible para asignaciones.
     * 
     * @return true si está activa y en estado DISPONIBLE, false en caso contrario
     */
    public boolean isAvailable() {
        return isActive() && status == CrewStatus.DISPONIBLE;
    }
    
    /**
     * Verifica si la cuadrilla está atendiendo una novedad.
     * 
     * @return true si está en estado EN_ATENCION, false en caso contrario
     */
    public boolean isInAttention() {
        return status == CrewStatus.EN_ATENCION;
    }
    
    /**
     * Verifica si se pueden modificar los miembros de la cuadrilla.
     * 
     * @return true si está disponible, false en caso contrario
     */
    public boolean allowsMemberModifications() {
        return isAvailable();
    }
    
    /**
     * Marca la cuadrilla como eliminada.
     */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cambia el estado de la cuadrilla.
     * 
     * @param newStatus Nuevo estado
     */
    public void changeStatus(CrewStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    // ========== equals y hashCode ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crew crew = (Crew) o;
        return Objects.equals(id, crew.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Crew{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", active=" + isActive() +
                '}';
    }
}
