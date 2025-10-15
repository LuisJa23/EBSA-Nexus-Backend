package co.com.ebsa.ebsa_nexus.application.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para respuestas de error cuando múltiples campos tienen valores duplicados.
 * Permite que el frontend muestre todos los errores de validación al mismo tiempo.
 */
public record MultipleDuplicateFieldsResponse(
    String code,
    String message,
    Map<String, String> validationErrors,
    LocalDateTime timestamp
) {
    /**
     * Constructor conveniente que establece el timestamp automáticamente.
     */
    public MultipleDuplicateFieldsResponse(String code, String message, Map<String, String> validationErrors) {
        this(code, message, validationErrors, LocalDateTime.now());
    }
}
