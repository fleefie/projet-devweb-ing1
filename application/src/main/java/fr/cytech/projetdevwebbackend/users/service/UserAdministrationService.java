package fr.cytech.projetdevwebbackend.users.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cytech.projetdevwebbackend.errors.types.UserAdministrationError;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.RoleRepository;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.util.Either;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Service handling user administration operations such as account
 * approval/acceptance.
 * <p>
 * This service manages administrative operations on user accounts,
 * including role assignments and account status changes.
 * <p>
 * Note: In this application:
 * - "Verified" means a user has verified their email address
 * - "Accepted" means a user's PENDING role has been removed by an administrator
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Service
@Slf4j
public class UserAdministrationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    /**
     * Standard role names used in the application.
     */
    private static final String ROLE_PENDING = "PENDING";
    private static final String ROLE_USER = "USER";

    /**
     * Creates a new UserAdministrationService with required dependencies.
     *
     * @param roleRepository Repository for role management
     * @param userRepository Repository for user management
     */
    @Autowired
    public UserAdministrationService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Approves a pending user account by changing their role from PENDING to USER.
     * <p>
     * This operation accepts a user by removing the PENDING role and adding the
     * USER role.
     * Note that this is different from email verification - a user can have a
     * verified email
     * but still be pending administrative acceptance.
     *
     * @param username The username of the user to approve
     * @return An Either containing an error or the updated user if successful
     */
    @Transactional
    public Either<UserAdministrationError, User> acceptUser(String username) {
        log.info("Processing user acceptance request for username: {}", username);

        // Fetch required entities
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            log.warn("User acceptance failed: user '{}' not found", username);
            return Either.left(UserAdministrationError.USER_NOT_FOUND);
        }

        Role pendingRole = getRoleOrNull(ROLE_PENDING);
        Role userRole = getRoleOrNull(ROLE_USER);

        // Validate roles exist in system
        if (pendingRole == null) {
            log.error("User acceptance failed: PENDING role not found in the system");
            return Either.left(UserAdministrationError.PENDING_ROLE_NOT_FOUND);
        }

        if (userRole == null) {
            log.error("User acceptance failed: USER role not found in the system");
            return Either.left(UserAdministrationError.USER_ROLE_NOT_FOUND);
        }

        // Check if user is already accepted (doesn't have PENDING role)
        if (user.isAccepted()) {
            log.info("User '{}' is already accepted", username);
            return Either.left(UserAdministrationError.USER_ALREADY_VERIFIED);
        }

        // Update user roles
        user.removeRole(pendingRole);
        user.addRole(userRole);

        // Save changes
        User savedUser = userRepository.save(user);
        log.info("User '{}' has been successfully approved", username);

        return Either.right(savedUser);
    }

    /**
     * Retrieves a role by name or returns null if not found.
     *
     * @param roleName The name of the role to retrieve
     * @return The Role object or null if not found
     */
    private Role getRoleOrNull(String roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }

    /**
     * Deletes a user by name.
     *
     * @param username The name of the user to delete
     * @return Nothing if the user was deleted, an error if the user could not be
     *         deleted
     */
    @Transactional
    public Optional<UserAdministrationError> deleteUser(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
        if (user != null) {
            userRepository.delete(user);
            return Optional.empty();
        } else {
            return Optional.of(UserAdministrationError.USER_NOT_FOUND);
        }
    }

    /**
     * Adds a role to a user.
     * 
     * @param username The name of the user to modify
     * @param roleName the role
     * @return Nothing if the operation was sucessful, an error if not
     */
    @Transactional
    public Optional<UserAdministrationError> addRole(String username, String roleName) {
        return userRepository.findByUsernameOrEmail(username, username)
                .map(user -> {
                    return roleRepository.findByName(roleName).map(role -> {
                        user.addRole(role);
                        return Optional.<UserAdministrationError>empty();
                    })
                            .orElseGet(() -> {
                                return Optional.of(UserAdministrationError.ROLE_NOT_FOUND);
                            });
                })
                .orElseGet(() -> {
                    return Optional.of(UserAdministrationError.USER_NOT_FOUND);
                });
    }

    /**
     * Deletes a role from a user.
     * 
     * @param username The name of the user to modify
     * @param roleName the role
     * @return Nothing if the operation was sucessful, an error if not
     */
    @Transactional
    public Optional<UserAdministrationError> deleteRole(String username, String roleName) {
        return userRepository.findByUsernameOrEmail(username, username)
                .map(user -> {
                    return roleRepository.findByName(roleName).map(role -> {
                        user.removeRole(role);
                        return Optional.<UserAdministrationError>empty();
                    })
                            .orElseGet(() -> {
                                return Optional.of(UserAdministrationError.ROLE_NOT_FOUND);
                            });
                })
                .orElseGet(() -> {
                    return Optional.of(UserAdministrationError.USER_NOT_FOUND);
                });
    }

    /**
     * Adds to the user's score
     * 
     * @param username The name of the user to modify
     * @param delta    Score differential to apply
     * @return Nothing if the operation was sucessful, an error if not
     */
    @Transactional
    public Optional<UserAdministrationError> addScore(String username, Integer delta) {
        return userRepository.findByUsernameOrEmail(username, username)
                .map(user -> {
                    user.addScore(delta);
                    return Optional.<UserAdministrationError>empty();
                })
                .orElseGet(() -> {
                    return Optional.of(UserAdministrationError.USER_NOT_FOUND);
                });
    }

    /**
     * Get a user's score.
     */
    public Integer getUserScore(String username) {
        return userRepository.findByUsernameOrEmail(username, username)
                .map(user -> {
                    return user.getPoints();
                })
                .orElse(0);
    }
}
