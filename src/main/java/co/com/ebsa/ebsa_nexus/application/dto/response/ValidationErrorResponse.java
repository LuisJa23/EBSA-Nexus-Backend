package co.com.ebsa.ebsa_nexus.application.dto.response;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de error de validación estructuradas.
 * Proporciona información detallada sobre errores de validación
 * para que el frontend pueda mostrar mensajes apropiados al usuario.
 */
public record ValidationErrorResponse(
    String code,
    String message,
    String field,
    String value,
    LocalDateTime timestamp
) {
    /**
     * Constructor conveniente que establece el timestamp automáticamente.
     */
    public ValidationErrorResponse(String code, String message, String field, String value) {
        this(code, message, field, value, LocalDateTime.now());
    }
}
