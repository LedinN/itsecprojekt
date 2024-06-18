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

    @DisplayName("Testing GET startpage with authorization")
    @Test
    @WithMockUser
    void test_GET_StartPage_Endpoint_With_Auth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("startpage"));
    }


    @DisplayName("Testing GET registration authorization")
    @Test
    @WithMockUser(username = "niick")
    void test_GET_Registration_Endpoint_WithoutAuth() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Testing GET register with authorization ")
    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void test_GET_Register_Page_Endpoint_WithAuth() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @DisplayName("Testing GET delete user without authorization")
    @Test
    @WithMockUser(username = "NIiiuuiiuick")
    void test_GET_Delete_User_Endpoint_Without_Auth () throws Exception {
        mockMvc.perform(get("http://localhost:8080/delete_user"))
                .andExpect(status().isForbidden());

    }

    @DisplayName("Testing GET delete user with authorization")
    @Test
    @WithMockUser(username = "OGADMIN", roles = {"ADMIN"})
    void test_GET_Delete_User_With_Auth () throws Exception {
        mockMvc.perform(get("http://localhost:8080/delete_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_user"));


    }



 /**TODO - TESTING POST*/

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


    @DisplayName("Testing POST update password - user not found")
    @Test
    @WithMockUser(username = "OGADMIN", roles = {"ADMIN"})
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