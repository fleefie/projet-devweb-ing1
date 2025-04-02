package fr.cytech.projetdevwebbackend.devices.dto;

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
public class DeviceSearchDto {
    @NotNull
    private String query;
}
