package fr.cytech.projetdevwebbackend.users.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for username and integer operations.
 * <p>
 * This DTO is used in API endpoints that require a username and an integer,
 * such as user lookup or administrative actions on a user account.
 *
 * @author fleefie
 * @since 2025-03-20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsernameIntegerDto {

    /**
     * The username of the user.
     * Must not be null or empty.
     */
    @NotNull(message = "Username cannot be null")
    @Pattern(regexp = "[a-zA-Z0-9]*", message = "Username must be alphanumeric")
    private String username;

    /**
     * The integer to use.
     */
    @NotNull(message = "must provide a valid integer")
    @Getter
    private Integer integer;

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
