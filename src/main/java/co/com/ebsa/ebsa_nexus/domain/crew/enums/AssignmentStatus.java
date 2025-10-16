package co.com.ebsa.ebsa_nexus.domain.crew.enums;

/**
 * Estados posibles de una asignación de novedad a cuadrilla.
 * 
 * <p>El ciclo de vida de una asignación es:</p>
 * <pre>
 * ASIGNADO → EN_CURSO → COMPLETADO
 *    ↓
 * CANCELADO (desde cualquier estado)
 * </pre>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public enum AssignmentStatus {
    /**
     * Novedad asignada pero no iniciada.
     * Estado inicial cuando se crea la asignación.
     */
    ASIGNADO,
    
    /**
     * Cuadrilla trabajando activamente en la novedad.
     * Se marca cuando el jefe de cuadrilla inicia el trabajo.
     */
    EN_CURSO,
    
    /**
     * Trabajo completado exitosamente.
     * Estado final de una asignación exitosa.
     */
    COMPLETADO,
    
    /**
     * Asignación cancelada.
     * Puede ocurrir por reasignación o por otros motivos.
     */
    CANCELADO;
    
    /**
     * Verifica si la asignación está activa (no finalizada).
     * 
     * @return true si está en ASIGNADO o EN_CURSO, false en caso contrario
     */
    public boolean isActive() {
        return this == ASIGNADO || this == EN_CURSO;
    }
    
    /**
     * Verifica si la asignación ha finalizado.
     * 
     * @return true si está en COMPLETADO o CANCELADO, false en caso contrario
     */
    public boolean isFinished() {
        return this == COMPLETADO || this == CANCELADO;
    }
    
    /**
     * Verifica si se puede iniciar el trabajo.
     * 
     * @return true si está en ASIGNADO, false en caso contrario
     */
    public boolean canStart() {
        return this == ASIGNADO;
    }
    
    /**
     * Verifica si se puede completar el trabajo.
     * 
     * @return true si está en EN_CURSO, false en caso contrario
     */
    public boolean canComplete() {
        return this == EN_CURSO;
    }
    
    /**
     * Verifica si se puede cancelar la asignación.
     * 
     * @return true si no está finalizada, false en caso contrario
     */
    public boolean canCancel() {
        return !isFinished();
    }
}
