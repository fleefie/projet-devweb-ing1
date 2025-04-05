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
    PASSWORDS_DO_NOT_MATCH("Passwords do not match"),
    EMAIL_ALREADY_EXISTS("Email is already in use"),
    INVALID_BIRTHDATE_FORMAT("Invalid birthdate format. Please use YYYY-MM-DD"),
    DATABASE_ERROR("An error occurred while updating user information"),
    INVALID_NAME("Name must contain only alphanumeric characters and spaces"),
    INVALID_EMAIL("Email must be valid"),
    INVALID_PASSWORD("Password must be at least 15 characters long"),
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
