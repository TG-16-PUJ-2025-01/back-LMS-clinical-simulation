package co.edu.javeriana.lms.integration;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.javeriana.lms.accounts.dtos.ChangePasswordDto;
import co.edu.javeriana.lms.accounts.dtos.LoginDto;
import co.edu.javeriana.lms.accounts.dtos.LoginResponseDto;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.videos.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @Mock
    private VideoService videoService;

    @Mock
    private HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String token;
    private String email = "superadmin@gmail.com";
    private String password = "superadmin";

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private LoginResponseDto getAuthTokenString() throws Exception {
        LoginDto loginDto = new LoginDto(email, password);
        String loginRequest = objectMapper.writeValueAsString(loginDto);

        String response = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponseDto<LoginResponseDto> loginResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<LoginResponseDto>>() {}
        );
        
        return loginResponse.getData();
    }

    @BeforeAll
    public static void setUpAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    public void setUp() throws Exception {
        token = "Bearer " + getAuthTokenString().getToken();
    }

    @Test
    @Order(1)
    public void testLogin() throws Exception {
        LoginResponseDto loginResponse = getAuthTokenString();
        token = loginResponse.getToken();
        
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
        assertNotNull(loginResponse.getRoles());
        assert loginResponse.getRoles().size() == 4;
        assert token.startsWith("eyJ"); // JWT tokens typically start with 'eyJ'
    }

    @Test
    @Order(1)
    public void testValidateToken() throws Exception {
        mockMvc.perform(get("/auth/validate-token")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @Order(1)
    public void testGetRolesByToken() throws Exception {
        String response = mockMvc.perform(get("/auth/roles")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponseDto<String[]> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<String[]>>() {}
        );

        assertNotNull(response);
        assertNotNull(apiResponse.getData());
        assert apiResponse.getData().length == 4;
    }

    @Test
    @Order(1)
    public void testGetEmailByToken() throws Exception {
        String response = mockMvc.perform(get("/auth/email")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponseDto<String> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<String>>() {}
        );

        assertNotNull(response);
        assertNotNull(apiResponse.getData());
        assert apiResponse.getData().equals(email);
    }

    @Test
    @Order(1)
    public void testGetNameByToken() throws Exception {
        String response = mockMvc.perform(get("/auth/name")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponseDto<String> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<String>>() {}
        );

        assertNotNull(response);
        assertNotNull(apiResponse.getData());
        assert apiResponse.getData().equals("Sofía Admin");
    }

    @Test
    @Order(2)
    public void testChangePassword() throws Exception {
        String newPassword = "Newpassword123!";
        ChangePasswordDto changePasswordDto = new ChangePasswordDto(password, newPassword);
        String changePasswordRequest = objectMapper.writeValueAsString(changePasswordDto);

        String response = mockMvc.perform(post("/auth/change-password")
                .header("Authorization", token)
                .contentType("application/json")
                .content(changePasswordRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
    }
    
}
