package fr.cytech.projetdevwebbackend.errors.types;

/**
 * Abstract monadic error class
 *
 * @author fleefie
 * @since 2025-03-22
 */
public abstract interface Error {
    /**
     * Returns the error message
     */
    public abstract String getMessage();
}
