package co.com.ebsa.ebsa_nexus.application.crew.exceptions;

/**
 * Excepción lanzada cuando se intenta asignar una cuadrilla que ya tiene asignaciones activas.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class CrewAlreadyAssignedException extends RuntimeException {
    
    public CrewAlreadyAssignedException(Long crewId) {
        super("Crew " + crewId + " already has active assignments");
    }
    
    public CrewAlreadyAssignedException(Long crewId, int activeCount) {
        super(String.format("Crew %d has %d active assignments", crewId, activeCount));
    }
    
    public CrewAlreadyAssignedException(String message) {
        super(message);
    }
}
