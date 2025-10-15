package co.com.ebsa.ebsa_nexus.domain.exception;

/**
 * Exception thrown when attempting to create or update a User with a field value
 * that already exists in the database (e.g., duplicate email, phone, document).
 */
public class DuplicateFieldException extends RuntimeException {
    private final String field;
    private final String value;

    public DuplicateFieldException(String field, String value) {
        super(String.format("Ya existe un usuario con %s: %s", field, value));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
