package dev.nick.itsecprojekt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DTOUser {


    @NotBlank
    @Email(message = "Please enter a valid e-mail address")
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @Size( min = 2, max = 50)
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
