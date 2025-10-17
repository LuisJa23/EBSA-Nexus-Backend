package co.com.ebsa.ebsa_nexus.domain.exception.crew;

/**
 * Excepción lanzada cuando se intenta agregar un usuario a una cuadrilla
 * cuando ya está activo en otra cuadrilla.
 * 
 * <p>Regla de negocio: Un usuario solo puede estar activo en UNA cuadrilla a la vez.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class UserAlreadyInCrewException extends RuntimeException {
    
    public UserAlreadyInCrewException(Long userId) {
        super("User " + userId + " is already active in another crew");
    }
    
    public UserAlreadyInCrewException(Long userId, Long currentCrewId) {
        super(String.format("User %d is already active in crew %d", userId, currentCrewId));
    }
    
    public UserAlreadyInCrewException(String message) {
        super(message);
    }
}
