// TODO: HOLY FUCKING SHIT THIS IS A MESS
// Remake this using PROPER error handling.
// Using exceptions over Eithers and Optionals make my eyes bleed.

package fr.cytech.projetdevwebbackend.announcements.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cytech.projetdevwebbackend.announcements.dto.AnnouncementDto;
import fr.cytech.projetdevwebbackend.announcements.dto.AnnouncementIdDto;
import fr.cytech.projetdevwebbackend.announcements.dto.AnnouncementReportDto;
import fr.cytech.projetdevwebbackend.announcements.dto.AnnouncementSearchDto;
import fr.cytech.projetdevwebbackend.announcements.model.Announcement;
import fr.cytech.projetdevwebbackend.announcements.model.AnnouncementReport;
import fr.cytech.projetdevwebbackend.announcements.model.projections.AnnouncementReportProjection;
import fr.cytech.projetdevwebbackend.announcements.model.projections.AnnouncementSearchProjection;
import fr.cytech.projetdevwebbackend.announcements.model.repository.AnnouncementReportRepository;
import fr.cytech.projetdevwebbackend.announcements.model.repository.AnnouncementRepository;
import fr.cytech.projetdevwebbackend.announcements.service.AnnouncementReportService;
import fr.cytech.projetdevwebbackend.announcements.service.AnnouncementService;
import fr.cytech.projetdevwebbackend.users.jwt.JwtTokenProvider;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/announcements")
@Slf4j
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AnnouncementReportService reportService;
    private final AnnouncementReportRepository announcementReportRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementController(
            AnnouncementService announcementService,
            AnnouncementReportService reportService,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider, AnnouncementReportRepository announcementReportRepository,
            AnnouncementRepository announcementRepository) {
        this.announcementService = announcementService;
        this.reportService = reportService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.announcementReportRepository = announcementReportRepository;
        this.announcementRepository = announcementRepository;
    }

    // Convert Announcement to DTO
    private AnnouncementDto convertToDto(Announcement announcement) {
        return AnnouncementDto.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .body(announcement.getBody())
                .posterUsername(announcement.getPoster().getUsername())
                .tags(announcement.getTagsList())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .roleRestrictions(announcement.getRoleRestrictions().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * Creates a new announcement.
     * <p>
     * Requires USER role.
     *
     * @param token The authentication token
     * @param dto   DTO containing the announcement details
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createAnnouncement(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid AnnouncementDto dto) {

        log.debug("Creating announcement with title: {}", dto.getTitle());

        // Extract username from token
        String username = jwtTokenProvider.extractUsername(token).fold(
                err -> {
                    log.warn("Failed to extract username from token: {}", err.getMessage());
                    return null;
                },
                user -> user);

        if (username == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid authentication token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            Announcement announcement = announcementService.createAnnouncement(
                    dto.getTitle(),
                    dto.getBody(),
                    dto.getTags(),
                    user.get(),
                    dto.getRoleRestrictions(),
                    true);

            log.info("Announcement created successfully by user {}", username);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Announcement created successfully");
            response.put("announcementId", announcement.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating announcement: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Gets an announcement by ID.
     * <p>
     * Available to all users, including unauthenticated ones.
     * Visibility restrictions are enforced by the service.
     *
     * @param dto   DTO containing the announcement ID
     * @param token Optional authentication token
     * @return ResponseEntity with the announcement or error status
     */
    @PostMapping("/get")
    public ResponseEntity<?> getAnnouncement(
            @RequestBody @Valid AnnouncementIdDto dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.debug("Fetching announcement with ID: {}", dto.getId());

        User currentUser = null;

        // If token is provided, extract user
        if (token != null && !token.isEmpty()) {
            String username = jwtTokenProvider.extractUsername(token).fold(
                    err -> null,
                    user -> user);

            if (username != null) {
                currentUser = userRepository.findByUsername(username).orElse(null);
            }
        }

        try {
            Announcement announcement = announcementService.getAnnouncementById(dto.getId(), currentUser);

            log.info("Successfully retrieved announcement with ID: {}", dto.getId());

            return ResponseEntity.ok(convertToDto(announcement));
        } catch (Exception e) {
            log.warn("Failed to retrieve announcement: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Searches for announcements.
     * <p>
     * Available to all users, including unauthenticated ones.
     *
     * @param dto   DTO containing the search term
     * @param token Optional authentication token
     * @return ResponseEntity with search results or error status
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchAnnouncements(
            @RequestBody @Valid AnnouncementSearchDto dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.debug("Searching announcements with term: {}", dto.getSearchTerm());

        User currentUser = null;

        // If token is provided, extract user
        if (token != null && !token.isEmpty()) {
            String username = jwtTokenProvider.extractUsername(token).fold(
                    err -> null,
                    user -> user);

            if (username != null) {
                currentUser = userRepository.findByUsername(username).orElse(null);
            }
        }

        try {
            List<AnnouncementSearchProjection> results = announcementService.searchAnnouncements(dto.getSearchTerm(),
                    currentUser);

            log.info("Successfully searched announcements, found {} results", results.size());

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.warn("Failed to search announcements: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Updates an existing announcement.
     * <p>
     * Requires USER role.
     * Only the poster or an admin can update an announcement.
     *
     * @param token The authentication token
     * @param dto   DTO containing the updated announcement details
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update")
    public ResponseEntity<?> updateAnnouncement(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid AnnouncementDto dto) {

        log.debug("Updating announcement with ID: {}", dto.getId());

        // Extract username from token
        String username = jwtTokenProvider.extractUsername(token).fold(
                err -> {
                    log.warn("Failed to extract username from token: {}", err.getMessage());
                    return null;
                },
                user -> user);

        if (username == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid authentication token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            announcementService.updateAnnouncement(
                    dto.getId(),
                    dto.getTitle(),
                    dto.getBody(),
                    dto.getTags(),
                    user.get(),
                    dto.getRoleRestrictions(),
                    true);

            log.info("Announcement updated successfully by user {}", username);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Announcement updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating announcement: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Deletes an announcement.
     * <p>
     * Requires USER role.
     * Only the poster or an admin can delete an announcement.
     *
     * @param token The authentication token
     * @param dto   DTO containing the announcement ID
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/delete")
    public ResponseEntity<?> deleteAnnouncement(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid AnnouncementIdDto dto) {

        log.debug("Deleting announcement with ID: {}", dto.getId());

        // Extract username from token
        String username = jwtTokenProvider.extractUsername(token).fold(
                err -> {
                    log.warn("Failed to extract username from token: {}", err.getMessage());
                    return null;
                },
                user -> user);

        if (username == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid authentication token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            announcementService.deleteAnnouncement(dto.getId(), user.get());

            log.info("Announcement deleted successfully by user {}", username);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Announcement deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting announcement: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Reports an announcement.
     * <p>
     * Requires USER role.
     *
     * @param token The authentication token
     * @param dto   DTO containing the report details
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/report")
    public ResponseEntity<?> reportAnnouncement(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid AnnouncementReportDto dto) {

        log.debug("Reporting announcement with ID: {}", dto.getAnnouncementId());

        // Extract username from token
        String username = jwtTokenProvider.extractUsername(token).fold(
                err -> {
                    log.warn("Failed to extract username from token: {}", err.getMessage());
                    return null;
                },
                user -> user);

        if (username == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid authentication token");
            return ResponseEntity.status(401).body(errorResponse);
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            AnnouncementReport report = reportService.reportAnnouncement(
                    dto.getAnnouncementId(), dto.getReason(), user.get());

            log.info("Announcement reported successfully by user {}", username);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Announcement reported successfully");
            response.put("reportId", report.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error reporting announcement: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Gets all device reports or reports for a specific announcement.
     * <p>
     * Requires ADMIN role.
     *
     * @param idDto DTO optionally containing a announcement ID to filter by
     * @return ResponseEntity with the reports
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/get-reports")
    public ResponseEntity<?> getDeviceReports(@RequestBody @Valid AnnouncementIdDto idDto) {
        if (idDto.getId() == null) {
            // Return all reports if no device ID provided
            log.info("Admin retrieving all device reports");
            return ResponseEntity.ok(announcementReportRepository.findAll());
        } else {
            // Find the announcement
            Optional<Announcement> announcement = announcementRepository.findById(idDto.getId());
            if (announcement.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Announcement not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get reports for the announcement
            List<AnnouncementReportProjection> reports = announcementReportRepository
                    .findReportsByAnnouncementProjected(idDto.getId());

            log.info("Admin retrieved reports for device ID: {}", idDto.getId());
            return ResponseEntity.ok(reports);
        }
    }

}
