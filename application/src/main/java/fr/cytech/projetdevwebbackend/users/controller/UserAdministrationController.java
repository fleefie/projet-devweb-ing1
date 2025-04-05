package fr.cytech.projetdevwebbackend.users.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.cytech.projetdevwebbackend.errors.types.Error;
import fr.cytech.projetdevwebbackend.errors.types.UserAdministrationError;
import fr.cytech.projetdevwebbackend.users.dto.UserIdDto;
import fr.cytech.projetdevwebbackend.users.dto.UserReportDto;
import fr.cytech.projetdevwebbackend.users.dto.UserUpdateDto;
import fr.cytech.projetdevwebbackend.users.dto.UsernameDto;
import fr.cytech.projetdevwebbackend.users.dto.UsernameIntegerDto;
import fr.cytech.projetdevwebbackend.users.dto.UsernameRoleDto;
import fr.cytech.projetdevwebbackend.users.jwt.JwtTokenProvider;
import fr.cytech.projetdevwebbackend.users.model.Report;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.projections.ReportProjection;
import fr.cytech.projetdevwebbackend.users.model.repository.ReportRepository;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.users.service.UserAdministrationService;
import fr.cytech.projetdevwebbackend.util.Either;
import fr.cytech.projetdevwebbackend.util.services.ProfilePictureFileAccessService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for user administration operations.
 * <p>
 * This controller provides endpoints for administrative actions on user
 * accounts,
 * such as accepting pending users. All endpoints require administrative
 * privileges.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserAdministrationController {

    private final UserAdministrationService userAdministrationService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProfilePictureFileAccessService fileAccessService;
    private final ReportRepository reportRepository;

    @Value("${app.admin-username}")
    private String adminUsername;

    @Autowired
    public UserAdministrationController(UserAdministrationService userAdministrationService,
            UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
            ProfilePictureFileAccessService fileAccessService, ReportRepository reportRepository) {
        this.userAdministrationService = userAdministrationService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileAccessService = fileAccessService;
        this.reportRepository = reportRepository;
    }

    /**
     * Accepts a pending user, granting them standard user privileges.
     * <p>
     * This endpoint moves a user from PENDING status to active USER status.
     * Requires ADMIN role.
     *
     * @param usernameDto DTO containing the username to accept
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/accept-user")
    public ResponseEntity<?> acceptUser(@RequestBody @Valid UsernameDto usernameDto) {
        log.debug("Accepting user: {}", usernameDto.getUsername());

        if (usernameDto.getUsername().isBlank()) {
            log.warn("Attempt to accept user with blank username");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Username cannot be blank");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Either<UserAdministrationError, User> acceptationStatus = userAdministrationService
                .acceptUser(usernameDto.getUsername());

        return acceptationStatus.fold(
                // Handle error case
                error -> {
                    log.warn("Failed to accept user {}: {}", usernameDto.getUsername(), error);

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", error.getMessage());

                    return ResponseEntity.badRequest().body(errorResponse);
                },
                // Handle success case
                user -> {
                    log.info("User accepted successfully: {}", usernameDto.getUsername());

                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "User accepted successfully");

                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Deletes a user.
     * <p>
     * Requires ADMIN role.
     *
     * @param usernameDto DTO containing the username to delete
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestBody @Valid UsernameDto usernameDto) {

        // That would be quite a funny feature to have :-)
        if (usernameDto.getUsername().equals(adminUsername)) {
            log.warn("Tried to delete administrator acount");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unable to delete administrator account");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Try to delete the user
        Optional<UserAdministrationError> ret = userAdministrationService.deleteUser(usernameDto.getUsername());
        return ret.map(
                err -> {
                    log.warn("Failed to delete user {}: {}", usernameDto.getUsername(), err.getMessage());

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", err.getMessage());

                    return ResponseEntity.badRequest().body(errorResponse);

                })
                .orElseGet(
                        () -> {
                            log.info("User deleted successfully: {}", usernameDto.getUsername());

                            Map<String, Object> response = new HashMap<>();
                            response.put("message", "User deleted successfully");

                            return ResponseEntity.ok(response);
                        });
    }

    /**
     * Adds a role to a user.
     * <p>
     * Requires ADMIN role.
     *
     * @param usernameRoleDto DTO containing the username and role to work with
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/remove-role")
    public ResponseEntity<?> deleteRole(@RequestBody @Valid UsernameRoleDto usernameRoleDto) {

        if (usernameRoleDto.getUsername().equals(adminUsername)) {
            log.warn("Tried to edit administrator acount");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unable to edit administrator account");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Try to edit the user
        Optional<UserAdministrationError> ret = userAdministrationService.deleteRole(usernameRoleDto.getUsername(),
                usernameRoleDto.getRole());
        return ret.map(
                err -> {
                    log.warn("Failed to edit user {} with role {}: {}", usernameRoleDto.getUsername(),
                            usernameRoleDto.getRole(), err.getMessage());

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", err.getMessage());

                    return ResponseEntity.badRequest().body(errorResponse);

                })
                .orElseGet(
                        () -> {
                            log.info("User {}'s role {} removed successfully", usernameRoleDto.getUsername(),
                                    usernameRoleDto.getRole());

                            Map<String, Object> response = new HashMap<>();
                            response.put("message", "User edited successfully");

                            return ResponseEntity.ok(response);
                        });
    }

    /**
     * Adds a role to a user.
     * <p>
     * Requires ADMIN role.
     *
     * @param usernameRoleDto DTO containing the username and role to work with
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-role")
    public ResponseEntity<?> addRole(@RequestBody @Valid UsernameRoleDto usernameRoleDto) {

        if (usernameRoleDto.getUsername().equals(adminUsername)) {
            log.warn("Tried to edit administrator acount");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unable to edit administrator account");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Try to edit the user
        Optional<UserAdministrationError> ret = userAdministrationService.deleteRole(usernameRoleDto.getUsername(),
                usernameRoleDto.getRole());
        return ret.map(
                err -> {
                    log.warn("Failed to edit user {} with role {}: {}", usernameRoleDto.getUsername(),
                            usernameRoleDto.getRole(), err.getMessage());

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", err.getMessage());

                    return ResponseEntity.badRequest().body(errorResponse);

                })
                .orElseGet(
                        () -> {
                            log.info("User {} was given the role {} successfully", usernameRoleDto.getUsername(),
                                    usernameRoleDto.getRole());

                            Map<String, Object> response = new HashMap<>();
                            response.put("message", "User edited successfully");

                            return ResponseEntity.ok(response);
                        });
    }

    /**
     * Gets every user in the system.
     *
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/search-users")
    public ResponseEntity<?> searchUsers(@RequestBody @Valid UsernameDto usernameDto) {
        return ResponseEntity.ok(userRepository.searchByName(usernameDto.getUsername()));
    }

    /**
     * Gets every user in the system, with all information.
     *
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search-users-admin")
    public ResponseEntity<?> searchUsersAdmin(@RequestBody @Valid UsernameDto usernameDto) {
        return ResponseEntity.ok(userRepository.searchByNameAdmin(usernameDto.getUsername()));
    }

    /**
     * Change a user's score.
     * <p>
     * Requires ADMIN role.
     *
     * @param usernameIntegerDto DTO containing the username and score differential
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit-score")
    public ResponseEntity<?> addRole(@RequestBody @Valid UsernameIntegerDto usernameIntegerDto) {

        // Try to edit the user
        Optional<UserAdministrationError> ret = userAdministrationService.addScore(usernameIntegerDto.getUsername(),
                usernameIntegerDto.getInteger());
        return ret.map(
                err -> {
                    log.warn("Failed to edit user {}: {}", usernameIntegerDto.getUsername(), err.getMessage());

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", err.getMessage());

                    return ResponseEntity.badRequest().body(errorResponse);

                })
                .orElseGet(
                        () -> {
                            log.info("User {}'s score was edited successfully", usernameIntegerDto.getUsername());

                            Map<String, Object> response = new HashMap<>();
                            response.put("message", "User edited successfully");

                            return ResponseEntity.ok(response);
                        });
    }

    /**
     * Gets a provided user's profile picture
     */
    @PostMapping("/get-profile-picture")
    public ResponseEntity<?> getProfilePicture(@RequestHeader("Authorization") String token,
            @RequestBody @Valid UsernameDto usernameDto) {

        // Check if the user is logged in
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.ok(fileAccessService.getDefaultImage());
        }
        return userAdministrationService.getUserProfilePicture(usernameDto.getUsername())
                .fold(
                        err -> {
                            log.warn("Failed to get profile picture for user {}: {}", usernameDto.getUsername(),
                                    err.getMessage());

                            Map<String, Object> errorResponse = new HashMap<>();
                            errorResponse.put("message", err.getMessage());

                            return ResponseEntity.badRequest().body(errorResponse);
                        },
                        image -> {
                            log.info("Successfully retrieved profile picture for user {}", usernameDto.getUsername());
                            return ResponseEntity.ok(image);
                        });
    }

    /**
     * Uploads a profile picture for the current user
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/upload-profile-picture", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfilePicture(@RequestHeader("Authorization") String token,
            @RequestParam("image") MultipartFile image) {

        String username = jwtTokenProvider.extractUsername(token).fold(
                err -> {
                    log.warn("Failed to extract username from token: {}", err.getMessage());
                    return null;
                },
                user -> {
                    return user;
                });
        if (username == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid authentication token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        return userAdministrationService.setUserProfilePicture(username, image).map(
                err -> {
                    log.warn("Failed to upload profile picture for user {}: {}", username, err.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", err.getMessage());
                    return ResponseEntity.badRequest().body(errorResponse);
                }).orElseGet(
                        () -> {
                            log.info("Successfully uploaded profile picture for user {}", username);
                            Map<String, Object> response = new HashMap<>();
                            response.put("message", "Profile picture uploaded successfully");
                            return ResponseEntity.ok(response);
                        });
    }

    /**
     * Creates a report for a user.
     * <p>
     * Requires USER role.
     *
     * @param token     The authentication token
     * @param reportDto DTO containing the reported username and reason
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-report")
    public ResponseEntity<?> createReport(@RequestHeader("Authorization") String token,
            @RequestBody @Valid UserReportDto reportDto) {

        // Extract the reporter's username from the token
        String reporterUsername = jwtTokenProvider.extractUsername(token).fold(
                err -> {
                    log.warn("Failed to extract username from token: {}", err.getMessage());
                    return null;
                },
                user -> user);

        if (reporterUsername == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid authentication token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        // Validate that users exist
        Optional<User> reporter = userRepository.findByUsername(reporterUsername);
        Optional<User> reported = userRepository.findByUsername(reportDto.getReportedUsername());

        if (reporter.isEmpty() || reported.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Check if user is trying to report themselves
        if (reporterUsername.equals(reportDto.getReportedUsername())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "You cannot report yourself");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Create and save the report
        Report report = new Report(reporter.get(), reported.get(), reportDto.getReason());

        try {
            reportRepository.save(report);
            log.info("User {} reported user {} for: {}", reporterUsername, reportDto.getReportedUsername(),
                    reportDto.getReason());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Report submitted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating report: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Could not create report. User may already be reported.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Gets all reports or reports for a specific user.
     * <p>
     * Requires ADMIN role.
     *
     * @param usernameDto DTO optionally containing a username to filter by
     * @return ResponseEntity with the reports
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/get-reports")
    public ResponseEntity<?> getReports(@RequestBody @Valid UsernameDto usernameDto) {
        if (usernameDto.getUsername() == null || usernameDto.getUsername().isBlank()) {
            // Return all reports if no username provided
            log.info("Admin retrieving all reports");
            return ResponseEntity.ok(reportRepository.findAll());
        } else {
            // Find the user
            Optional<User> user = userRepository.findByUsername(usernameDto.getUsername());
            if (user.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "User not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get reports made by the user and reports received by the user
            List<ReportProjection> reportsMade = reportRepository.findReportsMadeByUserProjected(user.get().getId());
            List<ReportProjection> reportsReceived = reportRepository
                    .findReportsReceivedByUserProjected(user.get().getId());

            Map<String, Object> response = new HashMap<>();
            response.put("reportsMade", reportsMade);
            response.put("reportsReceived", reportsReceived);

            log.info("Admin retrieved reports for user: {}", usernameDto.getUsername());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Deletes a report.
     * <p>
     * Requires ADMIN role.
     *
     * @param reportIdDto DTO containing the ID of the report to delete
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete-report")
    public ResponseEntity<?> deleteReport(@RequestBody @Valid UserIdDto reportIdDto) {
        Long reportId = Long.valueOf(reportIdDto.getValue());

        if (!reportRepository.existsById(reportId)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Report not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            reportRepository.deleteById(reportId);
            log.info("Admin deleted report with ID: {}", reportId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Report deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting report: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Could not delete report");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Updates user profile information.
     * <p>
     * Regular users can only update their own profiles, while admins can update any
     * user.
     * 
     * @param token JWT authentication token
     * @param dto   DTO containing updated user information
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
            @RequestBody @Validated({ Default.class, UserUpdateDto.OnUpdate.class }) UserUpdateDto dto) {

        // Extract username from token
        String currentUsername = jwtTokenProvider.extractUsername(token).fold(
                err -> {
                    log.warn("Failed to extract username from token: {}", err.getMessage());
                    return null;
                },
                username -> username);

        if (currentUsername == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid authentication token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        // Determine which username to update
        String usernameToUpdate;
        if (dto.getUsername() != null && !dto.getUsername().equals(currentUsername)) {
            // Admin trying to update another user
            boolean isAdmin = userRepository.findByUsername(currentUsername)
                    .map(user -> user.hasRole("ADMIN"))
                    .orElse(false);

            if (!isAdmin) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "You can only update your own profile");
                return ResponseEntity.status(403).body(errorResponse);
            }

            usernameToUpdate = dto.getUsername();
        } else {
            // User updating their own profile
            usernameToUpdate = currentUsername;
        }

        // Call service to update user
        Optional<Error> result = userAdministrationService.updateUser(usernameToUpdate, dto);

        return result.map(
                err -> {
                    log.warn("Failed to update user {}: {}", usernameToUpdate, err.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", err.getMessage());
                    return ResponseEntity.badRequest().body(errorResponse);
                })
                .orElseGet(() -> {
                    log.info("User {} updated successfully", usernameToUpdate);
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "User updated successfully");
                    return ResponseEntity.ok(response);
                });
    }
}
