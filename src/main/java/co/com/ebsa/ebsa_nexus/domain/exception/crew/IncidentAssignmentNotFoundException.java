package co.com.ebsa.ebsa_nexus.domain.exception.crew;

/**
 * Excepción lanzada cuando no se encuentra una asignación de incidente.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class IncidentAssignmentNotFoundException extends RuntimeException {
    
    public IncidentAssignmentNotFoundException(Long id) {
        super("Incident assignment not found with id: " + id);
    }
    
    public IncidentAssignmentNotFoundException(String message) {
        super(message);
    }
}
