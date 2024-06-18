package dev.nick.itsecprojekt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/* PasswordUpdateDTO är en Data Transfer Object-klass som används för att hantera uppdatering av lösenord.
   Den innehåller två fält: email och newPassword.
   email: En giltig e-postadress som inte får vara tom.
   newPassword: Ett nytt lösenord som måste vara mellan 8 och 20 tecken långt och inte får vara tomt.
*/

public class PasswordUpdateDTO {

    @NotBlank
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String newPassword;

    // Getter-metod för email-fältet.
    public String getEmail() {
        return email;
    }

    // Getter-metod för newPassword-fältet.
    public String getNewPassword() {
        return newPassword;
    }

    // Setter-metod för email-fältet.
    public void setEmail(String email) {
        this.email = email;
    }

    // Setter-metod för newPassword-fältet.
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
