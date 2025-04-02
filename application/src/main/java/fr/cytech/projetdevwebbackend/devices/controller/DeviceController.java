package fr.cytech.projetdevwebbackend.devices.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.cytech.projetdevwebbackend.devices.dto.DeviceDto;
import fr.cytech.projetdevwebbackend.devices.dto.DeviceSearchDto;
import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.devices.model.repository.DeviceRepository;
import fr.cytech.projetdevwebbackend.devices.services.DeviceManagementService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/devices")
@Slf4j
public class DeviceController {

    private final DeviceManagementService deviceService;
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceController(DeviceManagementService deviceService, DeviceRepository deviceRepository) {
        this.deviceService = deviceService;
        this.deviceRepository = deviceRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DeviceDto dto) {
        log.info("Creating device: {}", dto.getName());
        Device saved = deviceService.createDevice(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        log.debug("Fetching device with id: {}", id);
        Optional<Device> found = deviceService.getDevice(id);
        return found.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DeviceDto dto) {
        log.debug("Updating device with id: {}", id);
        Optional<Device> updated = deviceService.updateDevice(id, dto);
        return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("Deleting device with id: {}", id);
        boolean deleted = deviceService.deleteDevice(id);
        return deleted
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Gets every user in the system.
     *
     * @return ResponseEntity with success or error status
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestBody @Valid DeviceSearchDto dto) {
        return ResponseEntity.ok(deviceService.searchDevices(dto.getQuery()));
    }
}
