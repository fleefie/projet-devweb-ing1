package fr.cytech.projetdevwebbackend.users.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cytech.projetdevwebbackend.users.model.User;

/**
 * Repository for managing User entities.
 * <p>
 * This repository provides methods for querying user data,
 * including lookups by username and email.
 *
 * @author fleefie
 * @since 2025-03-15
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username or email.
     *
     * @param username The username to search for
     * @param email    The email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Finds a user by username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email The email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username The username to check
     * @return true if a user exists with this username, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users that have a specific role.
     *
     * @param roleName The name of the role
     * @return List of users that have the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Finds all users that need email verification.
     *
     * @return List of users with unverified emails
     */
    List<User> findByVerifiedFalse();

    /**
     * Finds users by verification status and orders them by creation date.
     *
     * @param verified The verification status to filter by
     * @param pageable Pagination and sorting information
     * @return Page of users matching the criteria
     */
    Page<User> findByVerified(Boolean verified, Pageable pageable);
}
