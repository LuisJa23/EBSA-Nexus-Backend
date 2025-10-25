package co.com.ebsa.ebsa_nexus.domain.exception.crew;

/**
 * Excepción lanzada cuando no se encuentra un miembro de cuadrilla.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class CrewMemberNotFoundException extends RuntimeException {
    
    public CrewMemberNotFoundException(Long id) {
        super("Crew member not found with id: " + id);
    }
    
    public CrewMemberNotFoundException(Long crewId, Long userId) {
        super(String.format("Crew member not found for crew %d and user %d", crewId, userId));
    }
    
    public CrewMemberNotFoundException(String message) {
        super(message);
    }
}
