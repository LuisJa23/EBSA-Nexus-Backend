package co.com.ebsa.ebsa_nexus.domain.enums;

/**
 * Estados posibles de una cuadrilla de trabajo.
 * 
 * <p>Una cuadrilla puede estar:</p>
 * <ul>
 *   <li><b>DISPONIBLE:</b> Lista para recibir asignaciones</li>
 *   <li><b>EN_ATENCION:</b> Atendiendo una novedad activamente</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public enum CrewStatus {
    /**
     * Cuadrilla disponible para recibir asignaciones.
     * Estado inicial de toda cuadrilla recién creada.
     */
    DISPONIBLE,
    
    /**
     * Cuadrilla atendiendo una novedad.
     * No se pueden modificar miembros en este estado.
     */
    EN_ATENCION;
    
    /**
     * Verifica si el estado permite modificaciones de estructura.
     * 
     * @return true si se pueden agregar/quitar miembros, false en caso contrario
     */
    public boolean allowsModifications() {
        return this == DISPONIBLE;
    }
    
    /**
     * Verifica si el estado permite asignaciones.
     * 
     * @return true si se pueden asignar novedades, false en caso contrario
     */
    public boolean allowsAssignments() {
        return this == DISPONIBLE;
    }
}