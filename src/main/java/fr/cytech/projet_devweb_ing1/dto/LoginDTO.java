package fr.cytech.projet_devweb_ing1.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Registration form.
 */
public class LoginDTO {

    @NotEmpty
    @NotNull
    private String loginUsername;

    @NotEmpty
    @NotNull
    private String loginPassword;

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String username) {
        this.loginUsername = username;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }
}
