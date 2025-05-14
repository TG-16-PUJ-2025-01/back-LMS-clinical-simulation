package co.edu.javeriana.lms.integration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.javeriana.lms.videos.services.VideoService;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class VideoIntegrationTest {

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

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
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
                .contentType("application/json")
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
    }

    @Test
    @Order(1)
    public void testSearchVideos() throws Exception {
        mockMvc.perform(get("/video/all?page=0&size=15&sort=name&asc=true&filter=")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(12)))
                .andExpect(jsonPath("$.metadata.total", is(12)))
                .andExpect(jsonPath("$.metadata.size", is(12)))
                .andExpect(jsonPath("$.metadata.totalPages", is(1)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(2)
    public void testSearchVideosMultiplePages() throws Exception {
        mockMvc.perform(get("/video/all?page=0&size=5&sort=name&asc=true&filter=")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total", is(12)))
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.totalPages", is(3)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(3)
    public void testEditVideoSuccess() throws Exception {
        mockMvc.perform(put("/video/1").header("Authorization", token)
                .contentType("application/json")
                .content("{\"name\":\"Video 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Video 1")))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }

    @Test
    @Order(4)
    public void testEditVideoFailure() throws Exception {
        mockMvc.perform(put("/video/1000").header("Authorization", token)
                .contentType("application/json")
                .content("{\"name\":\"Video 3\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }

    
    @Test
    @Order(5)
    public void testDeleteVideosSuccess() throws Exception {
        mockMvc.perform(delete("/video/2").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.videoId", is(2)))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }

    @Test
    @Order(6)
    public void testDeleteVideosFailure() throws Exception {
        mockMvc.perform(delete("/video/1000").header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }
}
