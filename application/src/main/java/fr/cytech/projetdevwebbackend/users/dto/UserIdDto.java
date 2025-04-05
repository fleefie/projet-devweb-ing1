/**
 * DTO containing just an integer value.
 */
package fr.cytech.projetdevwebbackend.users.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserIdDto {
    @NotNull(message = "Value cannot be null")
    private int value;
}
