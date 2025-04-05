package fr.cytech.projetdevwebbackend.devices.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cytech.projetdevwebbackend.devices.dto.DeviceDto;
import fr.cytech.projetdevwebbackend.devices.dto.DeviceIdDto;
import fr.cytech.projetdevwebbackend.devices.dto.DeviceReportDto;
import fr.cytech.projetdevwebbackend.devices.dto.DeviceSearchDto;
import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.devices.model.DeviceReport;
import fr.cytech.projetdevwebbackend.devices.model.projections.DeviceReportProjection;
import fr.cytech.projetdevwebbackend.devices.model.repository.DeviceReportRepository;
import fr.cytech.projetdevwebbackend.devices.services.DeviceManagementService;
import fr.cytech.projetdevwebbackend.users.jwt.JwtTokenProvider;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.users.service.UserAdministrationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/devices")
@Slf4j
public class DeviceController {

    private final DeviceManagementService deviceService;
    private final UserRepository userRepository;
    private final UserAdministrationService userAdministrationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DeviceReportRepository deviceReportRepository;

    @Autowired
    public DeviceController(DeviceManagementService deviceService,
            UserAdministrationService userAdministrationService, JwtTokenProvider jwtTokenProvider,
            DeviceReportRepository deviceReportRepository, UserRepository userRepository) {
        this.deviceService = deviceService;
        this.userAdministrationService = userAdministrationService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.deviceReportRepository = deviceReportRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createDevice(@RequestHeader("Authorization") String token,
            @RequestBody @Valid DeviceDto dto) {
        log.info("Creating device: {}", dto.getName());
        Device saved = deviceService.createDevice(dto);
        jwtTokenProvider.extractUsername(token).fold(
                err -> {
                },
                username -> {
                    userAdministrationService.addScore(username, 1);
                });
        return ResponseEntity.ok(Map.of("message", "Device created successfully", "device", saved));
    }

    @PostMapping("/get")
    public ResponseEntity<?> getDevice(@RequestHeader("Authorization") String token,
            @RequestBody @Valid DeviceIdDto idDto) {
        log.debug("Fetching device with id: {}", idDto.getId());
        Optional<Device> found = deviceService.getDevice(idDto.getId());
        return found.map(device -> {
            jwtTokenProvider.extractUsername(token).fold(
                    err -> {
                    },
                    username -> {
                        userAdministrationService.addScore(username, 1);
                    });
            return ResponseEntity.ok(Map.of("message", "Device fetched successfully", "device", device));
        }).orElseGet(() -> {
            log.warn("Device with id {} not found", idDto.getId());
            return ResponseEntity.badRequest().body(Map.of("message", "Device not found"));
        });
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateDevice(@RequestHeader("Authorization") String token,
            @RequestBody @Valid DeviceDto dto) {
        log.debug("Updating device with id: {}", dto.getId());
        Optional<Device> updated = deviceService.updateDevice(dto.getId(), dto);
        return updated.map(device -> {
            jwtTokenProvider.extractUsername(token).fold(
                    err -> {
                    },
                    username -> {
                        userAdministrationService.addScore(username, 1);
                    });
            return ResponseEntity.ok(Map.of("message", "Device updated successfully", "device", device));
        }).orElseGet(() -> {
            log.warn("Device with id {} not found", dto.getId());
            return ResponseEntity.badRequest().body(Map.of("message", "Device not found"));
        });
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteDevice(@RequestHeader("Authorization") String token,
            @RequestBody @Valid DeviceIdDto idDto) {
        log.debug("Deleting device with id: {}", idDto.getId());
        boolean deleted = deviceService.deleteDevice(idDto.getId());
        if (deleted) {
            jwtTokenProvider.extractUsername(token).fold(
                    err -> {
                    },
                    username -> {
                        userAdministrationService.addScore(username, 1);
                    });
            return ResponseEntity.ok(Map.of("message", "Device deleted successfully"));
        } else {
            log.warn("Device with id {} not found", idDto.getId());
            return ResponseEntity.badRequest().body(Map.of("message", "Device not found"));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchDevices(@RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody @Valid DeviceSearchDto dto) {
        log.debug("Searching devices with name: {}", dto.getQuery());
        var found = deviceService.searchDevices(dto.getQuery());
        // Increase user score if the token contains a user
        jwtTokenProvider.extractUsername(token).fold(
                err -> {
                },
                username -> {
                    userAdministrationService.addScore(username, 1);
                });
        return ResponseEntity.ok(Map.of("message", "Devices fetched successfully", "devices", found));
    }

    /**
     * Creates a report for a device.
     * <p>
     * Requires USER role.
     *
     * @param token     The authentication token
     * @param reportDto DTO containing the device ID and reason
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/report")
    public ResponseEntity<?> reportDevice(@RequestHeader("Authorization") String token,
            @RequestBody @Valid DeviceReportDto reportDto) {

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

        // Validate that user and device exist
        Optional<User> reporter = userRepository.findByUsername(reporterUsername);
        Optional<Device> device = deviceService.getDevice(reportDto.getDeviceId());

        if (reporter.isEmpty() || device.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User or device not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Create and save the report
        DeviceReport report = new DeviceReport(reporter.get(), device.get(), reportDto.getReason());

        try {
            deviceReportRepository.save(report);
            log.info("User {} reported device ID {} for: {}", reporterUsername, reportDto.getDeviceId(),
                    reportDto.getReason());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Device report submitted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating device report: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Could not create report. Device may already be reported by this user.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Gets all device reports or reports for a specific device.
     * <p>
     * Requires ADMIN role.
     *
     * @param idDto DTO optionally containing a device ID to filter by
     * @return ResponseEntity with the reports
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/get-reports")
    public ResponseEntity<?> getDeviceReports(@RequestBody @Valid DeviceIdDto idDto) {
        if (idDto.getId() == null) {
            // Return all reports if no device ID provided
            log.info("Admin retrieving all device reports");
            return ResponseEntity.ok(deviceReportRepository.findAll());
        } else {
            // Find the device
            Optional<Device> device = deviceService.getDevice(idDto.getId());
            if (device.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Device not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get reports for the device
            List<DeviceReportProjection> reports = deviceReportRepository.findReportsByDeviceProjected(idDto.getId());

            log.info("Admin retrieved reports for device ID: {}", idDto.getId());
            return ResponseEntity.ok(reports);
        }
    }

    /**
     * Deletes a device report.
     * <p>
     * Requires ADMIN role.
     *
     * @param idDto DTO containing the ID of the report to delete
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete-report")
    public ResponseEntity<?> deleteDeviceReport(@RequestBody @Valid DeviceIdDto idDto) {
        if (!deviceReportRepository.existsById(idDto.getId())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Device report not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            deviceReportRepository.deleteById(idDto.getId());
            log.info("Admin deleted device report with ID: {}", idDto.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Device report deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting device report: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Could not delete device report");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
