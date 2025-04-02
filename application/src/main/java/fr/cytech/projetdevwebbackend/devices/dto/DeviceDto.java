package fr.cytech.projetdevwebbackend.devices.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for transferring device information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    private Map<String, Object> properties;
}
