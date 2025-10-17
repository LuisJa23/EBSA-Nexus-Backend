package co.com.ebsa.ebsa_nexus.domain.exception.crew;

/**
 * Excepción lanzada cuando se intenta remover al último miembro de una cuadrilla.
 * 
 * <p>Regla de negocio: Una cuadrilla debe tener al menos 1 miembro activo.</p>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class CannotRemoveLastMemberException extends RuntimeException {
    
    public CannotRemoveLastMemberException(Long crewId) {
        super("Cannot remove last member from crew " + crewId);
    }
    
    public CannotRemoveLastMemberException(String message) {
        super(message);
    }
}
