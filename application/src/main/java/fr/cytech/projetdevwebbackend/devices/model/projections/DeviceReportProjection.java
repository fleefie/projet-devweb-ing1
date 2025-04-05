package fr.cytech.projetdevwebbackend.devices.model.projections;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Projection interface for DeviceReport entities.
 * Used for serializing device reports to JSON in a simplified format.
 */
public interface DeviceReportProjection {
    Long getId();

    String getReporterUsername();

    Long getDeviceId();

    String getDeviceName();

    String getReason();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getReportDate();
}
