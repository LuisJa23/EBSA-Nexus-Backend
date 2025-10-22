package co.com.ebsa.ebsa_nexus.domain.enums;

/**
 * Estados del ciclo de vida de una novedad.
 * Flujo: CREADA → EN_CURSO → COMPLETADA → CERRADA
 *        CREADA → CANCELADA (solo desde CREADA)
 * 
 * @author EBSA Nexus Team
 * @version 2.0
 * @since 2025-10-22
 */
public enum NoveltyStatus {
    /**
     * Novedad recién creada, sin cuadrilla asignada
     */
    CREADA,
    
    /**
     * Cuadrilla asignada, trabajo en progreso
     */
    EN_CURSO,
    
    /**
     * Trabajo completado por la cuadrilla
     */
    COMPLETADA,
    
    /**
     * Novedad finalizada con reporte completo
     */
    CERRADA,
    
    /**
     * Novedad cancelada (solo desde estado CREADA)
     */
    CANCELADA;
    
    /**
     * Verifica si se puede asignar una cuadrilla
     */
    public boolean canAssignCrew() {
        return this == CREADA;
    }
    
    /**
     * Verifica si se puede cancelar
     */
    public boolean canBeCancelled() {
        return this == CREADA;
    }
    
    /**
     * Verifica si se pueden subir evidencias
     */
    public boolean canUploadEvidence() {
        return this == EN_CURSO;
    }
    
    /**
     * Verifica si se puede completar
     */
    public boolean canComplete() {
        return this == EN_CURSO;
    }
    
    /**
     * Verifica si se puede cerrar
     */
    public boolean canClose() {
        return this == COMPLETADA;
    }
    
    /**
     * Verifica si es un estado terminal
     */
    public boolean isTerminal() {
        return this == CERRADA || this == CANCELADA;
    }
    
    /**
     * Valida si la transición al estado objetivo es válida.
     * 
     * @param targetStatus Estado objetivo de la transición
     * @return true si la transición es válida, false en caso contrario
     */
    public boolean canTransitionTo(NoveltyStatus targetStatus) {
        return switch (this) {
            case CREADA -> targetStatus == EN_CURSO || targetStatus == CANCELADA;
            case EN_CURSO -> targetStatus == COMPLETADA || targetStatus == CANCELADA;
            case COMPLETADA -> targetStatus == CERRADA;
            case CERRADA, CANCELADA -> false; // Estados terminales
        };
    }
}
