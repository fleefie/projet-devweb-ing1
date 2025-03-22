package fr.cytech.projetdevwebbackend.errors.types;

/**
 * Enum representing errors that can occur during device admin operations.
 *
 * @author fleefie
 * @since 2025-03-22
 */
public enum DeviceAdministrationError implements fr.cytech.projetdevwebbackend.errors.types.Error {
    DEVICE_NOT_FOUND("The specified device was not found in the system");

    private final String message;

    DeviceAdministrationError(String message) {
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
