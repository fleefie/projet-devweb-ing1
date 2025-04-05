package fr.cytech.projetdevwebbackend.users.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.cytech.projetdevwebbackend.errors.types.Error;
import fr.cytech.projetdevwebbackend.errors.types.FileError;
import fr.cytech.projetdevwebbackend.errors.types.UserAdministrationError;
import fr.cytech.projetdevwebbackend.users.dto.UserUpdateDto;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.RoleRepository;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.util.Either;
import fr.cytech.projetdevwebbackend.util.services.ProfilePictureFileAccessService;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class UserAdministrationService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfilePictureFileAccessService profilePictureFileAccessService;

    /**
     * Standard role names used in the application.
     */
    private static final String ROLE_PENDING = "PENDING";
    private static final String ROLE_USER = "USER";

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

    /**
     * Gets a user's profile picture.
     * Profile pictures as stored under the database as "id.png".
     */
    public Either<Error, byte[]> getUserProfilePicture(String username) {
        return userRepository.findByUsernameOrEmail(username, username)
                .map(user -> {
                    return profilePictureFileAccessService.read(user.getId()).fold(
                            error -> {
                                log.error("Error reading file: {}", error);
                                return Either.<Error, byte[]>left(error);
                            },
                            optionalBytes -> optionalBytes.map(Either::<Error, byte[]>right)
                                    .orElseGet(() -> {
                                        try {
                                            return Either.<Error, byte[]>right(
                                                    profilePictureFileAccessService.getDefaultImage());
                                        } catch (Exception e) {
                                            return Either.<Error, byte[]>left(FileError.GENERAL_IO_ERROR);
                                        }
                                    }));
                })
                .orElseGet(() -> {
                    log.error("User not found: {}", username);
                    return Either.<Error, byte[]>left(UserAdministrationError.USER_NOT_FOUND);
                });
    }

    public Optional<Error> setUserProfilePicture(String username, MultipartFile image) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return Optional.of(UserAdministrationError.USER_NOT_FOUND);
            }

            byte[] imageBytes = image.getBytes();
            Long id = userOpt.get().getId();
            return profilePictureFileAccessService.store(id, imageBytes).map(Optional::<Error>of)
                    .orElseGet(() -> {
                        return Optional.empty();
                    });
        } catch (IOException e) {
            log.error("Error processing profile picture: {}", e.getMessage());
            return Optional.of(FileError.GENERAL_IO_ERROR);
        }
    }

    /**
     * Updates a user's information.
     * 
     * @param username Current username of the user
     * @param dto      Data Transfer Object containing the new user information
     * @return Optional of Error if there's a problem, empty Optional if successful
     */
    @Transactional
    public Optional<Error> updateUser(String username, UserUpdateDto dto) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.of(UserAdministrationError.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        boolean changes = false;

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                return Optional.of(UserAdministrationError.INVALID_NAME);
            }

            if (!dto.getName().matches("[a-zA-Z0-9 ]+")) {
                return Optional.of(UserAdministrationError.INVALID_NAME);
            }

            if (dto.getName().length() < 1 || dto.getName().length() > 100) {
                return Optional.of(UserAdministrationError.INVALID_NAME);
            }

            user.setName(dto.getName());
            changes = true;
        }

        if (dto.getEmail() != null) {
            if (dto.getEmail().isBlank()) {
                return Optional.of(UserAdministrationError.INVALID_EMAIL);
            }

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!dto.getEmail().matches(emailRegex)) {
                return Optional.of(UserAdministrationError.INVALID_EMAIL);
            }

            if (!dto.getEmail().equals(user.getEmail())) {
                Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
                if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                    return Optional.of(UserAdministrationError.EMAIL_ALREADY_EXISTS);
                }

                user.setEmail(dto.getEmail());
                changes = true;
            }
        }

        if (dto.getPassword() != null) {
            if (dto.getPassword().isBlank()) {
                return Optional.of(UserAdministrationError.INVALID_PASSWORD);
            }

            if (dto.getPassword().length() < 15) {
                return Optional.of(UserAdministrationError.INVALID_PASSWORD);
            }

            if (dto.getPasswordConfirm() == null || !dto.getPassword().equals(dto.getPasswordConfirm())) {
                return Optional.of(UserAdministrationError.PASSWORDS_DO_NOT_MATCH);
            }

            String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            changes = true;
        }

        if (dto.getBirthdate() != null) {
            if (dto.getBirthdate().isBlank()) {
                return Optional.of(UserAdministrationError.INVALID_BIRTHDATE_FORMAT);
            }

            if (!dto.getBirthdate().matches("\\d{4}-\\d{2}-\\d{2}")) {
                return Optional.of(UserAdministrationError.INVALID_BIRTHDATE_FORMAT);
            }

            try {
                LocalDate birthdate = LocalDate.parse(dto.getBirthdate());
                user.setBirthdate(birthdate.toString());
                changes = true;
            } catch (DateTimeParseException e) {
                return Optional.of(UserAdministrationError.INVALID_BIRTHDATE_FORMAT);
            }
        }

        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
            changes = true;
        }

        if (changes) {
            try {
                userRepository.save(user);
                log.info("User '{}' information updated successfully", username);
            } catch (Exception e) {
                log.error("Error updating user: {}", e.getMessage(), e);
                return Optional.of(UserAdministrationError.DATABASE_ERROR);
            }
        } else {
            log.info("No changes to update for user '{}'", username);
        }

        return Optional.empty();
    }
}
