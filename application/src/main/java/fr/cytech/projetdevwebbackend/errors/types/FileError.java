package fr.cytech.projetdevwebbackend.errors.types;

/**
 * Enum representing errors that can occur during file I/O access
 *
 * @author fleefie
 * @since 2025-03-22
 */
public enum FileError implements fr.cytech.projetdevwebbackend.errors.types.Error {
    GENERAL_IO_ERROR("Error during file access"),
    BAD_JSON("Object is bad JSON"),
    JSON_PROCESSING("Unable to serialize object");

    private final String message;

    FileError(String message) {
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
