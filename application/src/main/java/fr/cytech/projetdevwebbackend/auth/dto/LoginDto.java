package fr.cytech.projetdevwebbackend.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotNull
    @NotEmpty
    private String usernameOrEmail;
    @NotEmpty
    @NotNull
    private String password;
}
