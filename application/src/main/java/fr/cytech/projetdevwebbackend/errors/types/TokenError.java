package fr.cytech.projetdevwebbackend.errors.types;

/**
 * Enum representing token-related errors.
 *
 * @author fleefie
 * @since 2025-03-15
 */
public enum TokenError implements Error {
    EXPIRED_TOKEN("Token has expired"),
    INVALID_SIGNATURE("Invalid token signature"),
    MALFORMED_TOKEN("Malformed token"),
    UNSUPPORTED_TOKEN("Unsupported token"),
    EMPTY_CLAIMS("Token claims string is empty"),
    GENERIC_TOKEN_ERROR("Error processing token"),
    INVALID_TOKEN("Invalid token");

    private final String message;

    TokenError(String message) {
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
