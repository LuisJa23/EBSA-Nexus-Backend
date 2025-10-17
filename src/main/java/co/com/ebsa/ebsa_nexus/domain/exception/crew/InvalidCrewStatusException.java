package co.com.ebsa.ebsa_nexus.domain.exception.crew;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;

/**
 * Excepción lanzada cuando se intenta realizar una operación con un estado de cuadrilla inválido.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class InvalidCrewStatusException extends RuntimeException {
    
    public InvalidCrewStatusException(CrewStatus currentStatus, String operation) {
        super(String.format("Cannot %s: crew is in status %s", operation, currentStatus));
    }
    
    public InvalidCrewStatusException(String message) {
        super(message);
    }
}
