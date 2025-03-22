package fr.cytech.projetdevwebbackend.errors.types;

/**
 * Enum representing errors that can occur during user administration
 * operations.
 *
 * @author fleefie
 * @since 2025-03-15
 */
public enum UserAdministrationError implements Error {
    USER_NOT_FOUND("The specified user was not found in the system"),
    PENDING_ROLE_NOT_FOUND("PENDING role not found in the system"),
    USER_ROLE_NOT_FOUND("USER role not found in the system"),
    USER_ALREADY_VERIFIED("User is already verified"),
    USER_ALREADY_ACCEPTED("User is already accepted"),
    ROLE_NOT_FOUND("Role does not exist");

    private final String message;

    UserAdministrationError(String message) {
        this.message = message;
    }

    /**
     * Gets the human-readable error message.
     *
     * @return The error message
     */
    public String getMessage() {
        return message;
    }
}
