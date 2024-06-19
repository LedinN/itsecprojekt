package dev.nick.itsecprojekt.controller;

import dev.nick.itsecprojekt.controller.MyController;
import dev.nick.itsecprojekt.persistence.MyUser;
import dev.nick.itsecprojekt.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
    UserRepository userRepository;

    private MyUser testUser;

    /** Vi skapar upp ett objekt som vi kan använda för att mocka data  */
    @BeforeEach
    void setup() {
        testUser = new MyUser();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole("USER");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setAge(30);

        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
    }

    /**TODO - TESTING GET*/

    /*Testar att vi kommer in på startsidan med autentisering där all roller är tillåtna */
    @DisplayName("Testing GET startpage with authorization")
    @Test
    @WithMockUser
    void test_GET_StartPage_Endpoint_With_Auth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("startpage"));
    }
    /*Testar om vi kommer in på registrerings sidan som en vanlig användare utan admin rollen*/
    @DisplayName("Testing GET registration authorization")
    @Test
    @WithMockUser(username = "niick")
    void test_GET_Registration_Endpoint_WithoutAuth() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isForbidden());
    }
    /*Testar om det går att nå registrerings sidan med admin roll*/
    @DisplayName("Testing GET register with authorization ")
    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void test_GET_Register_Page_Endpoint_WithAuth() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
    /*Testar om en användare kommer åt registrerings sidan utan admin roll*/
    @DisplayName("Testing GET delete user without authorization")
    @Test
    @WithMockUser(username = "NIiiuuiiuick")
    void test_GET_Delete_User_Endpoint_Without_Auth () throws Exception {
        mockMvc.perform(get("http://localhost:8080/delete_user"))
                .andExpect(status().isForbidden());

    }
    /*Testar om en användare kommer åt registrerings sidan med admin roll*/
    @DisplayName("Testing GET delete user with authorization")
    @Test
    @WithMockUser(username = "OGADMIN", roles = {"ADMIN"})
    void test_GET_Delete_User_With_Auth () throws Exception {
        mockMvc.perform(get("http://localhost:8080/delete_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_user"));


    }


    /**TODO - TESTING POST*/
    /*Testar om om vi kan ta bort en användare med admin roll*/
     @DisplayName("Testing POST delete user")
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

    @DisplayName("Testing POST invalid email ")
    @Test
    @WithMockUser(roles = {"ADMIN"})
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
    /*Testar om man kan uppdatera lösen ord på en användare med admin roll*/
     @DisplayName("Testing POST update password")
     @Test
     @WithMockUser(username = "OGADMIN" , roles = {"ADMIN"})
     void test_POST_Update_Password() throws Exception {
         mockMvc.perform(post("/update_password")
                         .with(csrf())
                         .param("email", "test@example.com")
                         .param("newPassword", "newpassword"))
                 .andExpect(status().isOk())
                 .andExpect(view().name("update_password_successful"))
                 .andExpect(model().attributeExists("successMessage"));
     }

    /*Testar felhanteringen i våran lösenords endpoint när inte en användare hittas */
    @DisplayName("Testing POST update password - user not found")
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void test_POST_Update_Password_Not_Found() throws Exception {
        Mockito.when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);
        mockMvc.perform(post("/update_password")
                        .with(csrf())
                        .param("email", "notfound@example.com")
                        .param("newPassword", "newpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("update_password"))
                .andExpect(model().attributeExists("errorMessage"));
    }
    /*Testar om vi kan ta bort en användare med admin roll*/
    @DisplayName("Testing POST delete user - successful deletion")
    @Test
    @WithMockUser(roles = "ADMIN")
    void test_POST_Delete_User_Endpoint_Success() throws Exception {
        mockMvc.perform(post("/delete_user")
                        .with(csrf())
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_success"))
                .andExpect(model().attributeExists("deletedUserEmail"));

        Mockito.verify(userRepository, Mockito.times(1)).delete(testUser);
    }
}