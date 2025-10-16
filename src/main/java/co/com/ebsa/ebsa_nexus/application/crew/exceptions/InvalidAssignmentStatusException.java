package co.com.ebsa.ebsa_nexus.application.crew.exceptions;

import co.com.ebsa.ebsa_nexus.domain.crew.enums.AssignmentStatus;

/**
 * Excepción lanzada cuando se intenta realizar una transición de estado inválida en una asignación.
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
public class InvalidAssignmentStatusException extends RuntimeException {
    
    public InvalidAssignmentStatusException(AssignmentStatus currentStatus, String operation) {
        super(String.format("Cannot %s: assignment is in status %s", operation, currentStatus));
    }
    
    public InvalidAssignmentStatusException(String message) {
        super(message);
    }
}
