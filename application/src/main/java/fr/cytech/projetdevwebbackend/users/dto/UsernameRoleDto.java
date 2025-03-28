package fr.cytech.projetdevwebbackend.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for username and role operations.
 * <p>
 * This DTO is used in API endpoints that require a username and a role,
 * such as administrative actions on a user account.
 *
 * @author fleefie
 * @since 2025-03-20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsernameRoleDto {

    /**
     * The username of the user.
     * Must not be null or empty.
     */
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "Username must be alphanumeric")
    private String username;

    /**
     * The role.
     * Must not be null or empty.
     */
    @NotNull(message = "Role cannot be null")
    @NotBlank(message = "Role cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "Role must be alphanumeric")
    private String role;

    /**
     * Returns a string representation of this DTO.
     *
     * @return A string containing the username
     */
    @Override
    public String toString() {
        return "UsernameDto{username='" + username + "'}";
    }
}
