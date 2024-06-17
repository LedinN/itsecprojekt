package dev.nick.itsecprojekt.controller;

import dev.nick.itsecprojekt.controller.MyController;
import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class MyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @DisplayName("Testing Authorization")
    @WithMockUser(username = "Nick")
    @Test
    void testRegistrationWithoutAuth() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }


    @WithMockUser(username = "Nick")
    @Test
    void testStartPageEndpointWithAuth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("startpage"));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void updatePassword() throws Exception {
        mockMvc.perform(get("/update_password"))
                .andExpect(status().isOk())
                .andExpect(view().name("update_password"));
    }

    @Test
    @WithMockUser(username = "Nick")
    void testUpdatePasswordWithoutAuth() throws Exception {
        mockMvc.perform(get("/update_password"))
                .andExpect(status().isForbidden());
    }
        
    @DisplayName("Testing registration Authorization")
    @WithMockUser(username = "niick")
    @Test
    void testRegistrationEndpointWithoutAuth() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testUpdateUserWithAuth() throws Exception {
        mockMvc.perform(get("/update_user"))
          .andExpect(status().isOk())
          .andExpect(view().name("update_user"));

    @Test
    @WithMockUser(username = "NIiiuuiiuick")
    void testDeleteUserEndpointWithoutAuth () throws Exception {
        mockMvc.perform(get("http://localhost:8080/delete_user"))

                .andExpect(status().isForbidden());
    }

    /* Ett test som testar om det går att delete en user.
    detta görs genom att vi har en mock user som är admin och vi skapar en user med en påhittad email.
    därefter ber vi mockito leta efter den i userRepo:t och sedan skicka tillbaka den när den har hittats.
    sedan gör vi en post till userRepo:t och ber om att ta bort användaren med den valda emailen.
    Vi kontrollerar också att vi blir redirected till rätt sida efter detta skett.
     */

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testSuccessfulDeleteUser() throws Exception {
        String userEmail = "user@example.com";
        MyUser user = new MyUser();
        user.setEmail(userEmail);

        Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(user);

        mockMvc.perform(post("/delete_user")
                        .param("email", userEmail)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_success"))
                .andExpect(model().attribute("deletedUserEmail", userEmail));
    }


    /* Test som testar en felaktig registrering, men vi kollar om den får status 200 från början när den går in på sidan, vilket stämmer.
    sedan så får vi också tillbaka att email adressen är felaktig och vi får ett nytt försök att ändra det. Vi får error på felmeddelandet.
    Varför vi har med isOK är för att vi aldrig får en 400 vid felaktig inmatning, utan det kommer en pop-up istället när man försöker regga.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void failedUserRegistrationInvalidEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .param("email", "invalid-email")
                        .param("password", "validPassword1")
                        .param("role", "USER")
                        .param("firstname", "John")
                        .param("lastname", "Doe")
                        .param("age", "25")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "Email"));
    }

    @Test
    @WithMockUser(username = "OGADMIN", roles = {"ADMIN"})
    void testDeleteUserEndpointWithAuth () throws Exception {
        mockMvc.perform(get("http://localhost:8080/delete_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_user"));


    }

    @


}