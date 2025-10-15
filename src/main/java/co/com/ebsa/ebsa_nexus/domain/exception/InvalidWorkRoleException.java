package co.com.ebsa.ebsa_nexus.domain.exception;

/**
 * Exception thrown when a WorkRole does not match the WorkType of a User.
 * For example, when trying to assign an EXTERNAL role to an INTERNAL worker.
 */
public class InvalidWorkRoleException extends RuntimeException {
    private final String workType;
    private final String workRole;

    public InvalidWorkRoleException(String workType, String workRole) {
        super(String.format("El rol '%s' no es válido para un trabajador %s", workRole, workType));
        this.workType = workType;
        this.workRole = workRole;
    }

    public String getWorkType() {
        return workType;
    }

    public String getWorkRole() {
        return workRole;
    }
}
