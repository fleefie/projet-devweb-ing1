package fr.cytech.projetdevwebbackend.users.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for user modification.
 * <p>
 * This DTO captures all necessary information to modify a new user account,
 * including validation constraints to ensure data quality.
 * Field validations only apply when the field is not null.
 *
 * @author fleefie
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "password", "passwordConfirm" })
public class UserUpdateDto {
    /**
     * Username for the account to modify.
     * Only needed for admin purposes, to modify another user.
     */
    @Nullable
    private String username;

    /**
     * Full name or display name of the user.
     * Can contain alphanumeric characters and spaces.
     * Validation only occurs if field is provided.
     */
    @Nullable
    @Pattern(regexp = "[a-zA-Z0-9 ]+", message = "Name must contain only alphanumeric characters and spaces", groups = OnUpdate.class)
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters", groups = OnUpdate.class)
    private String name;

    /**
     * Email address for the account.
     * Must be a valid email format.
     * Validation only occurs if field is provided.
     */
    @Nullable
    @Email(message = "Email must be valid", groups = OnUpdate.class)
    @Size(max = 255, message = "Email cannot exceed 255 characters", groups = OnUpdate.class)
    private String email;

    /**
     * Password for the account.
     * Must meet minimum security requirements.
     * Validation only occurs if field is provided.
     */
    @Nullable
    @Size(min = 15, message = "Password must be at least 15 characters long", groups = OnUpdate.class)
    private String password;

    /**
     * Password confirmation to ensure correct entry.
     * Must match the password field.
     */
    @Nullable
    private String passwordConfirm;

    /**
     * Birthdate of the user. Format must be YYYY-MM-DD.
     * Validation only occurs if field is provided.
     */
    @Nullable
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Birthdate must be in the format 'YYYY-MM-DD'", groups = OnUpdate.class)
    private String birthdate;

    /**
     * Gender of the user, simple String.
     */
    @Nullable
    private String gender;

    /**
     * Checks if the password and confirmation password match.
     *
     * @return true if passwords match, false otherwise
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }

    /**
     * Validation group for conditionally validating update fields.
     * Only applied to non-null fields.
     */
    public interface OnUpdate {
    }
}
