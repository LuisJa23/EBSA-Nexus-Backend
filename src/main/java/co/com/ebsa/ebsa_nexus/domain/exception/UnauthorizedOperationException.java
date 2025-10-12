package co.com.ebsa.ebsa_nexus.domain.exception;

/**
 * Excepción lanzada cuando un usuario intenta realizar una operación para la cual no tiene permisos.
 * Ejemplos: usuario no-admin tratando de crear usuarios, admin tratando de desactivarse a sí mismo.
 */
public class UnauthorizedOperationException extends RuntimeException {
    
    public UnauthorizedOperationException(String message) {
        super(message);
    }
    
    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}