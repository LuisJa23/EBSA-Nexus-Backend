package co.com.ebsa.ebsa_nexus.domain.exception.auth;

/**
 * Excepción lanzada cuando la contraseña actual proporcionada es incorrecta
 * o cuando la nueva contraseña no cumple con las validaciones de negocio.
 */
public class InvalidPasswordException extends RuntimeException {
    
    public InvalidPasswordException(String message) {
        super(message);
    }
    
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
