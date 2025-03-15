package fr.cytech.projetdevwebbackend.users.service;

import org.springframework.security.core.userdetails.UserDetails;

import fr.cytech.projetdevwebbackend.errors.types.AuthError;
import fr.cytech.projetdevwebbackend.util.Either;

/**
 * Functional interface for loading user details without exceptions.
 * <p>
 * This interface provides an alternative to Spring Security's
 * UserDetailsService
 * that uses functional error handling with Either instead of exceptions.
 *
 * @author fleefie
 * @since 2025-03-15
 */
public interface UserDetailsProvider {

    /**
     * Loads user details by username or email identifier.
     *
     * @param usernameOrEmail The username or email to load
     * @return Either containing an error enum or the user details
     */
    Either<AuthError, UserDetails> findUserDetails(String usernameOrEmail);
}
