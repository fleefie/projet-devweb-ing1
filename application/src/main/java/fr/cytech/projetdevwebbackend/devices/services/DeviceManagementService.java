package fr.cytech.projetdevwebbackend.devices.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cytech.projetdevwebbackend.devices.dto.DeviceDto;
import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.devices.model.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for handling devices.
 *
 * @author fleefie
 * @since 2025-03-24
 */
@Service
@Slf4j
public class DeviceManagementService {

    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceManagementService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device createDevice(DeviceDto dto) {
        Device device = new Device(dto.getName(), dto.getProperties());
        return deviceRepository.save(device);
    }

    public Optional<Device> getDevice(Long id) {
        return deviceRepository.findById(id);
    }

    public Optional<Device> updateDevice(Long id, DeviceDto dto) {
        return deviceRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setProperties(dto.getProperties());
            return deviceRepository.save(existing);
        });
    }

    public boolean deleteDevice(Long id) {
        return deviceRepository.findById(id).map(device -> {
            deviceRepository.delete(device);
            return true;
        }).orElse(false);
    }

    public List<Device> searchDevices(String searchText) {
        List<Device> byName = deviceRepository.findByNameContainingIgnoreCase(searchText);
        List<Device> byJson = deviceRepository.jsonSearchByValue(searchText);

        // Combine and remove duplicates
        return Stream.concat(byName.stream(), byJson.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
