package com.example.kanban;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedUserRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/boards"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginPageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Welcome Back!")));
    }

    @Test
    void registerPageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Create Your Account")));
    }

    @Test
    void registerUserWithValidDataRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "newuser")
                .param("email", "newuser@example.com")
                .param("password", "secret123")
                .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));
    }

    @Test
    void registerUserWithMismatchedPasswordShowsValidationError() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "testuser2")
                .param("email", "testuser2@example.com")
                .param("password", "secret123")
                .param("confirmPassword", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("user", "confirmPassword"));
    }
}
