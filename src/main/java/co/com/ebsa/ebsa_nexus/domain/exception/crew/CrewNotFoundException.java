package co.com.ebsa.ebsa_nexus.domain.exception.crew;

/**
 * Excepción lanzada cuando no se encuentra una cuadrilla.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class CrewNotFoundException extends RuntimeException {
    
    public CrewNotFoundException(Long id) {
        super("Crew not found with id: " + id);
    }
    
    public CrewNotFoundException(String code) {
        super("Crew not found with code: " + code);
    }
    
    public CrewNotFoundException(String field, String value) {
        super(String.format("Crew not found with %s: %s", field, value));
    }
}
