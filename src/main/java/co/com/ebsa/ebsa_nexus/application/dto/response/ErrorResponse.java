package co.com.ebsa.ebsa_nexus.application.dto.response;

import java.time.LocalDateTime;

/**
 * DTO genérico para respuestas de error.
 * Utilizado para errores no relacionados con validaciones específicas.
 */
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {
    /**
     * Constructor conveniente que establece el timestamp automáticamente.
     */
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}
