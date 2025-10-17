package co.com.ebsa.ebsa_nexus.domain.exception.auth;

/**
 * Excepción lanzada cuando un usuario no es encontrado en el sistema.
 * Esta excepción es parte de la capa de dominio y no debe depender de frameworks externos.
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long userId) {
        super("Usuario con ID " + userId + " no encontrado");
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}