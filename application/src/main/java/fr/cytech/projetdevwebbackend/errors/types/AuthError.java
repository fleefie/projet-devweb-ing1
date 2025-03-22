package fr.cytech.projetdevwebbackend.errors.types;

/**
 * class representing authentication-related errors.
 * 
 * @author fleefie
 * @since 2025-03-15
 */
public enum AuthError implements Error {
    // Login errors
    USER_DOES_NOT_EXIST("User does not exist"),
    INVALID_CREDENTIALS("Invalid username/email or password"),
    ACCOUNT_DISABLED("User account is disabled"),
    ACCOUNT_LOCKED("User account is locked"),
    ACCOUNT_NOT_ACCEPTED("User account is pending administrative acceptance"),
    EMAIL_NOT_VALIDATED("User email was not validated"),

    // Registration validation errors
    USER_EXISTS("Username or email already exists"),
    USERNAME_NOT_ALPHANUMERIC("Username must contain only alphanumeric characters"),
    NAME_NOT_ALPHANUMERIC_SPACE("Name must contain only alphanumeric characters and spaces"),
    INVALID_EMAIL_FORMAT("Invalid email format"),
    PASSWORD_TOO_SHORT("Password must be at least 15 characters long"),
    PASSWORD_TOO_SIMPLE("Password must include non-alphanumeric characters"),
    PASSWORD_NOT_ASCII("Password must contain only ASCII characters"),
    PASSWORDS_DO_NOT_MATCH("Passwords do not match"),
    USERNAME_ALREADY_EXISTS("Username is taken"),
    EMAIL_ALREADY_EXISTS("Email is taken"),

    // Empty field errors
    EMPTY_PASSWORD("Password cannot be empty"),
    EMPTY_USERNAME("Username cannot be empty"),
    EMPTY_EMAIL("Email cannot be empty"),
    EMPTY_NAME("Name cannot be empty"),

    // System errors
    AUTHENTICATION_ERROR("Authentication error occurred"),
    ROLE_NOT_FOUND("Required role not found");

    private final String message;

    AuthError(String message) {
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
