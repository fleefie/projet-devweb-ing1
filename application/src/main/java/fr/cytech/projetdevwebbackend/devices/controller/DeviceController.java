package fr.cytech.projetdevwebbackend.devices.controller;

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
import fr.cytech.projetdevwebbackend.devices.dto.DeviceSearchDto;
import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.devices.services.DeviceManagementService;
import fr.cytech.projetdevwebbackend.users.jwt.JwtTokenProvider;
import fr.cytech.projetdevwebbackend.users.service.UserAdministrationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/devices")
@Slf4j
public class DeviceController {

    private final DeviceManagementService deviceService;
    private final UserAdministrationService userAdministrationService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public DeviceController(DeviceManagementService deviceService,
            UserAdministrationService userAdministrationService, JwtTokenProvider jwtTokenProvider) {
        this.deviceService = deviceService;
        this.userAdministrationService = userAdministrationService;
        this.jwtTokenProvider = jwtTokenProvider;
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
    public ResponseEntity<?> searchDevices(@RequestHeader("Authorization") String token,
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
}
