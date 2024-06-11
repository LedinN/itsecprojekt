package dev.nick.itsecprojekt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordUpdateDTO {

    @NotBlank
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

