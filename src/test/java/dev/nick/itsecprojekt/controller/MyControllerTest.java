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

    @DisplayName("Testing Authorization")
    @WithMockUser(username = "niick")
    @Test
    void testRegistrationWithoutAuth() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isForbidden());
    }


    @WithMockUser( )
    @Test
    void testStartPageWithAuth() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("startpage"));
    }




    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser(username = "NIiiuuiiuick")
    void testDeleteUserWithoutAuth () throws Exception {
        mockMvc.perform(get("http://localhost:8080/delete_user"))
                .andExpect(status().isForbidden());

    }
}