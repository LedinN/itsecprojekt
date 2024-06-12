package dev.nick.itsecprojekt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class DTOUser {

    @NotBlank
    @Email(message = "Please enter a valid e-mail address")
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    private String role;

    @NotBlank
    @Size(min = 2, max = 50)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastname;

    @Min(0)
    @Max(120)
    private int age;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
