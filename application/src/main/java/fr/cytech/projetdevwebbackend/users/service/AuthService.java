package fr.cytech.projetdevwebbackend.users.service;

import fr.cytech.projetdevwebbackend.errors.types.AuthError;
import fr.cytech.projetdevwebbackend.users.JwtAuthResponse;
import fr.cytech.projetdevwebbackend.users.dto.LoginDto;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.util.Either;

/**
 * Interface defining authentication operations.
 * 
 * @author fleefie
 * @since 2025-03-15
 */
public interface AuthService {
    /**
     * Authenticates a user with their credentials.
     * 
     * @param loginDto DTO containing login credentials
     * @return Either containing an error or the JWT authentication response
     */
    Either<AuthError, JwtAuthResponse> login(LoginDto loginDto);

    /**
     * Registers a new user in the system.
     * 
     * @param username Username for the new account
     * @param password Password for the new account
     * @param email    Email address for the new account
     * @param name     Display name for the new account
     * @param doHash   Whether to hash the password (false only for testing)
     * @return Either containing an error or the created user
     */
    Either<AuthError, User> register(String username, String password, String email,
            String name, Boolean doHash);

    /**
     * Checks if a user exists by username or email.
     * 
     * @param username Username to check
     * @param email    Email to check
     * @return true if user exists, false otherwise
     */
    boolean doesUserExist(String username, String email);
}
