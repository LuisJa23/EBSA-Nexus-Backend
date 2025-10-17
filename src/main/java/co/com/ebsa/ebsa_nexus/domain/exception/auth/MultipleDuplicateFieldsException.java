package co.com.ebsa.ebsa_nexus.domain.exception.auth;

import java.util.Map;

/**
 * Exception thrown when multiple fields have duplicate values.
 * This allows returning all validation errors in a single response
 * for better user experience in forms.
 */
public class MultipleDuplicateFieldsException extends RuntimeException {
    private final Map<String, String> duplicateFields;

    public MultipleDuplicateFieldsException(Map<String, String> duplicateFields) {
        super("Se encontraron múltiples campos duplicados");
        this.duplicateFields = duplicateFields;
    }

    public Map<String, String> getDuplicateFields() {
        return duplicateFields;
    }
}
