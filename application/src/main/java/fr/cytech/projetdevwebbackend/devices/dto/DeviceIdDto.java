/**
 * DTO containing only the id of a device.
 */
package fr.cytech.projetdevwebbackend.devices.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DeviceIdDto {
    @NotNull
    @NotEmpty
    private Long id;
}
