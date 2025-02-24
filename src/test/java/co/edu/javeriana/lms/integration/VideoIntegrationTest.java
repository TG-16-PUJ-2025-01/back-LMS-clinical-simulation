package co.edu.javeriana.lms.integration;

import jakarta.servlet.http.HttpServletRequest;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import co.edu.javeriana.lms.services.VideoService;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
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
    public void testSearchVideos() throws Exception {
        mockMvc.perform(get("/video/all?page=0&size=15&sort=name&asc=true&filter="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(10)))
                .andExpect(jsonPath("$.metadata.total", is(10)))
                .andExpect(jsonPath("$.metadata.size", is(10)))
                .andExpect(jsonPath("$.metadata.totalPages", is(1)))
                .andExpect(jsonPath("$.metadata.page", is(0)))
                .andExpect(jsonPath("$.metadata.previous", is(nullValue())))
                .andExpect(jsonPath("$.metadata.next", is(nullValue())));
    }

    @Test
    public void testSearchVideosMultiplePages() throws Exception {
        mockMvc.perform(get("/video/all?page=0&size=5&sort=name&asc=true&filter="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total", is(10)))
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.totalPages", is(2)))
                .andExpect(jsonPath("$.metadata.page", is(0)))
                .andExpect(jsonPath("$.metadata.previous", is(nullValue())))
                .andExpect(jsonPath("$.metadata.next",
                        is("http://null/video/all?page=1&size=5&sort=name&asc=true&filter=")));
    }

    @Test
    public void testEditVideoSuccess() throws Exception {
        mockMvc.perform(put("/video/1")
                .contentType("application/json")
                .content("{\"name\":\"Video 1\",\"expirationDate\":\"2025-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Video 1")))
                .andExpect(jsonPath("$.data.expirationDate", is("2025-01-01T00:00:00.000+00:00")))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }

    @Test
    public void testEditVideoFailure() throws Exception {
        mockMvc.perform(put("/video/1000")
                .contentType("application/json")
                .content("{\"name\":\"Video 3\",\"expirationDate\":\"2025-01-01\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }

    @Test
    public void testDeleteVideosSuccess() throws Exception {
        mockMvc.perform(delete("/video/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.videoId", is(2)))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }

    @Test
    public void testDeleteVideosFailure() throws Exception {
        mockMvc.perform(delete("/video/1000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.metadata", is(nullValue())));
    }
}
