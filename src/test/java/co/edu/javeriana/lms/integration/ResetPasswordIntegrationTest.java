package co.edu.javeriana.lms.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.javeriana.lms.accounts.dtos.PasswordResetDto;
import co.edu.javeriana.lms.accounts.dtos.UsernameDto;
import co.edu.javeriana.lms.videos.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class ResetPasswordIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @Mock
    private VideoService videoService;

    @Mock
    private HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    String email = "superadmin@gmail.com";
    String newPassword = "NewPassword123!";

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    public static void setUpAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    public void testRequestPasswordReset() throws Exception {
        UsernameDto usernameDto = new UsernameDto(email);
        String requestBody = objectMapper.writeValueAsString(usernameDto);

        mockMvc.perform(post("/reset-password/request")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testVerifyPasswordReset() throws Exception {
        PasswordResetDto passwordResetDto = PasswordResetDto.builder()
                .email(email)
                .token("token")
                .build();
        String requestBody = objectMapper.writeValueAsString(passwordResetDto);

        mockMvc.perform(post("/reset-password/verify")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testResetPassword() throws Exception {
        PasswordResetDto passwordResetDto = PasswordResetDto.builder()
                .email(email)
                .token("token")
                .password(newPassword)
                .build();
        String requestBody = objectMapper.writeValueAsString(passwordResetDto);

        mockMvc.perform(post("/reset-password/reset")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());
    }
}
