package fr.cytech.projetdevwebbackend.devices.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.devices.model.DeviceReport;
import fr.cytech.projetdevwebbackend.devices.model.projections.DeviceReportProjection;
import fr.cytech.projetdevwebbackend.users.model.User;

/**
 * Repository for managing device reports.
 */
@Repository
public interface DeviceReportRepository extends JpaRepository<DeviceReport, Long> {

    List<DeviceReport> findByReporter(User reporter);

    List<DeviceReport> findByDevice(Device device);

    @Query("SELECT r.id as id, u.username as reporterUsername, d.id as deviceId, d.name as deviceName, " +
            "r.reason as reason, r.reportDate as reportDate " +
            "FROM DeviceReport r JOIN r.reporter u JOIN r.device d " +
            "WHERE u.id = :userId")
    List<DeviceReportProjection> findReportsByUserProjected(@Param("userId") Long userId);

    @Query("SELECT r.id as id, u.username as reporterUsername, d.id as deviceId, d.name as deviceName, " +
            "r.reason as reason, r.reportDate as reportDate " +
            "FROM DeviceReport r JOIN r.reporter u JOIN r.device d " +
            "WHERE d.id = :deviceId")
    List<DeviceReportProjection> findReportsByDeviceProjected(@Param("deviceId") Long deviceId);
}
