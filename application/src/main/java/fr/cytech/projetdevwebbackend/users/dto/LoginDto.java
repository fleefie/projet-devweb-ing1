package fr.cytech.projetdevwebbackend.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for user authentication.
 * <p>
 * This DTO captures the credentials needed for user login,
 * allowing authentication via either username or email.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class LoginDto {

    /**
     * Username or email used for authentication.
     * The system will attempt to find a user matching either field.
     */
    @NotNull(message = "Username/email cannot be null")
    @NotBlank(message = "Username/email cannot be blank")
    @Size(min = 3, max = 255, message = "Username/email must be between 3 and 255 characters")
    private String usernameOrEmail;

    /**
     * User's password for authentication.
     * Will be validated against the stored hash.
     */
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    private String password;

    /**
     * Creates a login request with the specified credentials.
     *
     * @param usernameOrEmail Username or email to authenticate
     * @param password        User's password
     * @return A new LoginDto instance
     */
    public static LoginDto of(String usernameOrEmail, String password) {
        return new LoginDto(usernameOrEmail, password);
    }
}
