package co.com.ebsa.ebsa_nexus.domain.enums;

/**
 * Estados posibles del ciclo de vida de una novedad.
 * 
 * <p>Una novedad sigue un flujo unidireccional:</p>
 * <ul>
 *   <li><b>REPORTED:</b> Novedad reportada por supervisor, esperando asignación</li>
 *   <li><b>ASSIGNED:</b> Cuadrilla asignada, esperando inicio de trabajo</li>
 *   <li><b>IN_PROGRESS:</b> Trabajo en proceso</li>
 *   <li><b>RESOLVED:</b> Trabajo finalizado por cuadrilla, esperando verificación</li>
 *   <li><b>CLOSED:</b> Verificada y cerrada por administrador</li>
 *   <li><b>CANCELLED:</b> Cancelada por administrador</li>
 * </ul>
 * 
 * <p><b>Reglas de transición:</b></p>
 * <ul>
 *   <li>REPORTED → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED</li>
 *   <li>RESOLVED puede volver a IN_PROGRESS si es rechazada</li>
 *   <li>CANCELLED puede ocurrir desde cualquier estado excepto CLOSED</li>
 * </ul>
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public enum NoveltyStatus {
    /**
     * Novedad reportada por supervisor, esperando asignación de cuadrilla.
     * Estado inicial de toda novedad.
     */
    REPORTED,
    
    /**
     * Cuadrilla asignada, esperando inicio de trabajo.
     */
    ASSIGNED,
    
    /**
     * Cuadrilla trabajando en la novedad.
     */
    IN_PROGRESS,
    
    /**
     * Trabajo completado por cuadrilla, pendiente de verificación administrativa.
     */
    RESOLVED,
    
    /**
     * Novedad verificada y cerrada por administrador.
     * Estado final del flujo exitoso.
     */
    CLOSED,
    
    /**
     * Novedad cancelada por administrador.
     * Estado terminal.
     */
    CANCELLED;
    
    /**
     * Verifica si el estado permite cancelación.
     * 
     * @return true si la novedad puede ser cancelada, false en caso contrario
     */
    public boolean isCancellable() {
        return this != CLOSED;
    }
    
    /**
     * Verifica si el estado permite asignación de cuadrilla.
     * 
     * @return true si se puede asignar cuadrilla, false en caso contrario
     */
    public boolean allowsCrewAssignment() {
        return this == REPORTED || this == ASSIGNED;
    }
    
    /**
     * Verifica si el estado permite reasignación de cuadrilla.
     * 
     * @return true si se puede reasignar cuadrilla, false en caso contrario
     */
    public boolean allowsCrewReassignment() {
        return this == ASSIGNED || this == IN_PROGRESS;
    }
    
    /**
     * Verifica si el estado permite subir imágenes de evidencia.
     * 
     * @return true si se pueden subir evidencias, false en caso contrario
     */
    public boolean allowsEvidenceUpload() {
        return this == REPORTED || this == ASSIGNED || this == IN_PROGRESS;
    }
    
    /**
     * Verifica si el estado permite generar reporte.
     * 
     * @return true si se puede generar reporte, false en caso contrario
     */
    public boolean allowsReportGeneration() {
        return this == IN_PROGRESS || this == RESOLVED;
    }
    
    /**
     * Verifica si el estado es terminal (no permite más cambios).
     * 
     * @return true si es un estado final, false en caso contrario
     */
    public boolean isTerminal() {
        return this == CLOSED || this == CANCELLED;
    }
    
    /**
     * Valida si la transición al estado objetivo es válida.
     * 
     * @param targetStatus Estado objetivo de la transición
     * @return true si la transición es válida, false en caso contrario
     */
    public boolean canTransitionTo(NoveltyStatus targetStatus) {
        return switch (this) {
            case REPORTED -> targetStatus == ASSIGNED || targetStatus == CANCELLED;
            case ASSIGNED -> targetStatus == IN_PROGRESS || targetStatus == CANCELLED;
            case IN_PROGRESS -> targetStatus == RESOLVED || targetStatus == CANCELLED;
            case RESOLVED -> targetStatus == CLOSED || targetStatus == IN_PROGRESS || targetStatus == CANCELLED;
            case CLOSED, CANCELLED -> false; // Estados terminales
        };
    }
}
