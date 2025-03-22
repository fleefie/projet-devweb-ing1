package fr.cytech.projetdevwebbackend.devices.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.jpa.repository.JsonRepository;

/**
 * Repository for managing Devices
 *
 * @author fleefie
 * @since 2025-03-22
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>, JsonRepository<Device, Long> {
}
