package com.marketlocalshops.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketlocalshops.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.findByEmail("testuser@gmail.com").ifPresent(user -> {
            userRepository.delete(user);
        });
    }

    @Test
    void testFullAuthFlow() throws Exception {
        // 1. Register a user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("testuser@gmail.com");
        registerRequest.setPassword("testpassword");
        registerRequest.setName("testuser");
        registerRequest.setRole("CUSTOMER");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. Try registering the same user again (should fail)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        // 3. Login with registered credentials
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("testuser@gmail.com");
        authRequest.setPassword("testpassword");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract token
        String responseBody = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        String token = authResponse.getToken();

        // 4. Access secure profile endpoint with JWT
        mockMvc.perform(get("/api/auth/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 5. Access profile endpoint without JWT (should fail)
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized());

        // 6. Access public endpoint (GET /api/categories) without JWT (should pass)
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());

        // 7. Logout
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }
}
