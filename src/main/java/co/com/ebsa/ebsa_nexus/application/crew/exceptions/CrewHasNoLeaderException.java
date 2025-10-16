package co.com.ebsa.ebsa_nexus.application.crew.exceptions;

/**
 * Excepción lanzada cuando una cuadrilla no tiene líder asignado.
 * 
 * <p>Regla de negocio: Toda cuadrilla activa debe tener exactamente 1 líder.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class CrewHasNoLeaderException extends RuntimeException {
    
    public CrewHasNoLeaderException(Long crewId) {
        super("Crew " + crewId + " has no leader assigned");
    }
    
    public CrewHasNoLeaderException(String message) {
        super(message);
    }
}
