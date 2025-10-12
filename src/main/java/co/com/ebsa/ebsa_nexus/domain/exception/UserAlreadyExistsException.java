package co.com.ebsa.ebsa_nexus.domain.exception;

/**
 * Excepción lanzada cuando se intenta crear un usuario que ya existe en el sistema.
 * Esto puede ocurrir cuando hay conflictos de email o username únicos.
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}