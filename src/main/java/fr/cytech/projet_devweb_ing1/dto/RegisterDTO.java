package fr.cytech.projet_devweb_ing1.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Registration form.
 */
public class RegisterDTO {

    @NotEmpty
    @NotNull
    private String registerUsername;

    @NotEmpty
    @NotNull
    private String registerPassword;

    @NotEmpty
    @NotNull
    private String registerPasswordConfirm;

    public String getRegisterUsername() {
        return registerUsername;
    }

    public void setRegisterUsername(String username) {
        this.registerUsername = username;
    }

    public String getRegisterPassword() {
        return registerPassword;
    }

    public void setRegisterPassword(String password) {
        this.registerPassword = password;
    }

    public String getRegisterPasswordConfirm() {
        return registerPasswordConfirm;
    }

    public void setRegisterPasswordConfirm(String passwordConfirm) {
        this.registerPasswordConfirm = passwordConfirm;
    }

    public boolean isPasswordConfirmed() {
        return registerPassword.equals(registerPasswordConfirm);
    }
}
