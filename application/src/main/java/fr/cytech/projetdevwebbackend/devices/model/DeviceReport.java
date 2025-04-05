package fr.cytech.projetdevwebbackend.devices.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import fr.cytech.projetdevwebbackend.users.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Entity representing a report made by a user against a device.
 */
@Entity
@Table(name = "device_reports", uniqueConstraints = @UniqueConstraint(columnNames = { "reporter_id", "device_id" }))
@Getter
@Setter
@NoArgsConstructor
public class DeviceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private String reason;

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate = LocalDateTime.now();

    public DeviceReport(@NonNull User reporter, @NonNull Device device, @NonNull String reason) {
        this.reporter = reporter;
        this.device = device;
        this.reason = reason;
    }
}
