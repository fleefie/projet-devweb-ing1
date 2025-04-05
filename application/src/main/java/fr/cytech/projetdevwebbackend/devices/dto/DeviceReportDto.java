package fr.cytech.projetdevwebbackend.devices.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for device report creation.
 */
@Data
@NoArgsConstructor
public class DeviceReportDto {

    @NotNull(message = "Device ID cannot be null")
    private Long deviceId;

    @NotNull(message = "Reason cannot be null")
    @NotBlank(message = "Reason cannot be blank")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
