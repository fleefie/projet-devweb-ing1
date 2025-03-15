package fr.cytech.projetdevwebbackend.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for user registration.
 * <p>
 * This DTO captures all necessary information to register a new user account,
 * including validation constraints to ensure data quality.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "password", "passwordConfirm" })
public class RegisterDto {

    /**
     * Username for the new account.
     * Must be alphanumeric with no spaces.
     */
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "[a-zA-Z0-9]+", message = "Username must contain only alphanumeric characters")
    private String username;

    /**
     * Full name or display name of the user.
     * Can contain alphanumeric characters and spaces.
     */
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Pattern(regexp = "[a-zA-Z0-9 ]+", message = "Name must contain only alphanumeric characters and spaces")
    private String name;

    /**
     * Email address for the account.
     * Must be a valid email format.
     */
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    /**
     * Password for the account.
     * Must meet minimum security requirements.
     */
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 15, message = "Password must be at least 15 characters long")
    private String password;

    /**
     * Password confirmation to ensure correct entry.
     * Must match the password field.
     */
    @NotNull(message = "Password confirmation cannot be null")
    @NotBlank(message = "Password confirmation cannot be blank")
    private String passwordConfirm;

    /**
     * Checks if the password and confirmation password match.
     *
     * @return true if passwords match, false otherwise
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}
