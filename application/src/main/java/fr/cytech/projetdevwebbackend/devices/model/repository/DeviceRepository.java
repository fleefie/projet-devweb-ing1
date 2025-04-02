package fr.cytech.projetdevwebbackend.devices.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.jpa.repository.JsonRepositoryFragment;

/**
 * Repository for managing Devices
 *
 * @author fleefie
 * @since 2025-03-22
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>, JsonRepositoryFragment<Device, Long> {
    List<Device> findByNameContainingIgnoreCase(String name);
}
