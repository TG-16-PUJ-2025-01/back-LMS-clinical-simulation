package co.edu.javeriana.lms.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class CalendarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    private static String token;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private String getAuthTokenString() throws Exception {
        String loginRequest = """
                {
                    "email": "superadmin@gmail.com",
                    "password": "superadmin"
                }
                """;

        String response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        String tokenString = jsonNode.get("data").get("token").asText();

        return "Bearer " + tokenString.trim();
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
    public void setup() throws Exception {
        token = getAuthTokenString();
        System.out.println("Token: " + token);
    }

    @Test
    @Order(1)
    public void getEvents_shouldReturnEventsSuccessfully() throws Exception {
        mockMvc.perform(get("/calendar?start=2025-01-01 00:00&end=2025-12-31 23:59")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Events retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(2)
    public void getAllEvents_shouldReturnAllEventsSuccessfully() throws Exception {
        mockMvc.perform(get("/calendar/all?start=2025-01-01 00:00&end=2025-12-31 23:59")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Events retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(3)
    public void getEvents_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/calendar?start=2025-01-01 00:00&end=2025-12-31 23:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    public void getEvents_withInvalidDateFormat_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/calendar?start=2025-01-01&end=2025-12-31")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void getEvents_missingStartParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/calendar?end=2025-12-31 23:59")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    public void getEvents_missingEndParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/calendar?start=2025-01-01 00:00")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
