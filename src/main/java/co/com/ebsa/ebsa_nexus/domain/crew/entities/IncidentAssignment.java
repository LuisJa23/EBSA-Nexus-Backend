package co.com.ebsa.ebsa_nexus.domain.crew.entities;

import co.com.ebsa.ebsa_nexus.domain.crew.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidad de dominio que representa la asignación de una novedad/incidente a una cuadrilla.
 * 
 * <p>Una asignación vincula una cuadrilla con una novedad específica que debe atender.
 * Incluye seguimiento del ciclo de vida: asignación → inicio → finalización.</p>
 * 
 * <p><b>Reglas de negocio principales:</b></p>
 * <ul>
 *   <li>Solo cuadrillas activas pueden recibir asignaciones</li>
 *   <li>Una novedad puede reasignarse cancelando la asignación anterior</li>
 *   <li>Solo el jefe de cuadrilla puede cambiar estados</li>
 *   <li>Se mantiene historial completo de todas las asignaciones</li>
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
public class IncidentAssignment {
    
    /**
     * Identificador único de la asignación.
     */
    private Long id;
    
    /**
     * ID de la cuadrilla asignada.
     */
    private Long crewId;
    
    /**
     * ID de la novedad/incidente asignado.
     */
    private Long incidentId;
    
    /**
     * ID del usuario que realizó la asignación.
     * Típicamente un ADMIN o JEFE_AREA.
     */
    private Long assignedBy;
    
    /**
     * Fecha y hora en que se realizó la asignación.
     */
    private LocalDateTime assignedAt;
    
    /**
     * Fecha y hora en que se inició el trabajo.
     * NULL si aún no ha iniciado.
     */
    private LocalDateTime startedAt;
    
    /**
     * Fecha y hora en que se completó el trabajo.
     * NULL si aún no ha completado.
     */
    private LocalDateTime completedAt;
    
    /**
     * Fecha y hora en que se canceló la asignación.
     * NULL si no ha sido cancelada.
     */
    private LocalDateTime cancelledAt;
    
    /**
     * Estado actual de la asignación.
     */
    private AssignmentStatus status;
    
    /**
     * Notas adicionales sobre la asignación.
     * Puede incluir observaciones, motivos de cancelación, etc.
     */
    private String notes;
    
    // ========== Métodos de Negocio ==========
    
    /**
     * Verifica si la asignación está activa (no finalizada).
     * 
     * @return true si está en ASIGNADO o EN_CURSO, false en caso contrario
     */
    public boolean isActive() {
        return status != null && status.isActive();
    }
    
    /**
     * Verifica si la asignación ha finalizado.
     * 
     * @return true si está en COMPLETADO o CANCELADO, false en caso contrario
     */
    public boolean isFinished() {
        return status != null && status.isFinished();
    }
    
    /**
     * Verifica si el trabajo ya fue iniciado.
     * 
     * @return true si startedAt no es null, false en caso contrario
     */
    public boolean isStarted() {
        return startedAt != null;
    }
    
    /**
     * Verifica si el trabajo ya fue completado.
     * 
     * @return true si completedAt no es null, false en caso contrario
     */
    public boolean isCompleted() {
        return completedAt != null;
    }
    
    /**
     * Verifica si la asignación fue cancelada.
     * 
     * @return true si cancelledAt no es null, false en caso contrario
     */
    public boolean isCancelled() {
        return cancelledAt != null;
    }
    
    /**
     * Calcula la duración del trabajo en minutos.
     * Solo aplicable si el trabajo está completado.
     * 
     * @return Duración en minutos entre inicio y finalización, o null si no aplica
     */
    public Long getDurationMinutes() {
        if (startedAt == null || completedAt == null) {
            return null;
        }
        return ChronoUnit.MINUTES.between(startedAt, completedAt);
    }
    
    /**
     * Calcula los minutos transcurridos desde la asignación.
     * 
     * @return Minutos desde assigned_at hasta ahora (o hasta la finalización)
     */
    public long getMinutesSinceAssignment() {
        LocalDateTime endDate = LocalDateTime.now();
        if (completedAt != null) {
            endDate = completedAt;
        } else if (cancelledAt != null) {
            endDate = cancelledAt;
        }
        return ChronoUnit.MINUTES.between(assignedAt, endDate);
    }
    
    /**
     * Inicia el trabajo de la asignación.
     */
    public void start() {
        this.startedAt = LocalDateTime.now();
        this.status = AssignmentStatus.EN_CURSO;
    }
    
    /**
     * Completa el trabajo de la asignación.
     */
    public void complete() {
        this.completedAt = LocalDateTime.now();
        this.status = AssignmentStatus.COMPLETADO;
    }
    
    /**
     * Cancela la asignación.
     * 
     * @param reason Motivo de la cancelación
     */
    public void cancel(String reason) {
        this.cancelledAt = LocalDateTime.now();
        this.status = AssignmentStatus.CANCELADO;
        this.notes = (this.notes != null ? this.notes + "\n" : "") + 
                     "Cancelado: " + reason;
    }
    
    /**
     * Agrega una nota a la asignación.
     * 
     * @param note Nota a agregar
     */
    public void addNote(String note) {
        this.notes = (this.notes != null ? this.notes + "\n" : "") + note;
    }
    
    // ========== equals y hashCode ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncidentAssignment that = (IncidentAssignment) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "IncidentAssignment{" +
                "id=" + id +
                ", crewId=" + crewId +
                ", incidentId=" + incidentId +
                ", status=" + status +
                ", active=" + isActive() +
                '}';
    }
}
