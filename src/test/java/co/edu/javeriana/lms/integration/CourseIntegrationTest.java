package co.edu.javeriana.lms.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class CourseIntegrationTest {

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
    public void getAllCourses() throws Exception {
        mockMvc.perform(get("/course/all?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Courses retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total").isNumber())
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(2)
    public void getCoursesByCoordinator() throws Exception {
        mockMvc.perform(get("/course/all/coordinator?sort=courseId&asc=true")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Courses retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(3)
    public void getCourseById() throws Exception {
        mockMvc.perform(get("/course/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Course retrieved successfully"))
                .andExpect(jsonPath("$.data.courseId").value(1));
    }

    @Test
    @Order(4)
    public void deleteCourseById() throws Exception {
        mockMvc.perform(delete("/course/delete/8")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Course deleted successfully."));
    }

    @Test
    @Order(5)
    public void deleteCourseByIdNotFound() throws Exception {
        mockMvc.perform(delete("/course/delete/999")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").value("Course not found with id: 999"));
    }

    @Test
    @Order(6)
    public void updateCourse() throws Exception {
        String updateRequest = """
                {
                    "courseId": 1,
                    "javerianaId": 12345,
                    "name": "Updated Course",
                    "coordinatorId": 4,
                    "classes": [],
                    "faculty": "Engineering",
                    "department": "Computer Science",
                    "program": "Software Engineering",
                    "semester": 8
                }
                """;

        mockMvc.perform(put("/course/update/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Course updated successfully."))
                .andExpect(jsonPath("$.data.name").value("Updated Course"));
    }

    @Test
    @Order(7)
    public void updateCourseNotFound() throws Exception {
        String updateRequest = """
                {
                    "name": "Nonexistent Course",
                    "javerianaId": 99999,
                    "faculty": "Engineering",
                    "department": "Computer Science",
                    "program": "Software Engineering",
                    "semester": 8
                }
                """;

        mockMvc.perform(put("/course/update/999")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"));
    }

    @Test
    @Order(8)
    public void addCourse() throws Exception {
        String addRequest = """
                {
                    "courseId": 1,
                    "javerianaId": 67890,
                    "coordinatorId": 4,
                    "name": "New Course",
                    "faculty": "Engineering",
                    "department": "Computer Science",
                    "program": "Software Engineering",
                    "semester": 6
                }
                """;

        mockMvc.perform(post("/course/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Course added successfully."))
                .andExpect(jsonPath("$.data.name").value("New Course"));
    }

    @Test
    @Order(9)
    public void addCourseInvalidData() throws Exception {
        String addRequest = """
                {
                    "name": "",
                    "javerianaId": null,
                    "faculty": "",
                    "department": "",
                    "program": "",
                    "semester": null
                }
                """;

        mockMvc.perform(post("/course/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @Order(10)
    public void getRecommendedRubricsByCourseId() throws Exception {
        mockMvc.perform(get("/course/recommend/1/rubrics")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rubrics retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @Order(11)
    public void getRecommendedRubricsByCourseIdNotFound() throws Exception {
        mockMvc.perform(get("/course/recommend/999/rubrics")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").value("Course with ID 999 not found"));
    }

    @Test
    @Order(12)
    public void getCourseByIdNotFound() throws Exception {
        mockMvc.perform(get("/course/999")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").value("Course with ID 999 not found"));
    }

    @Test
    @Order(13)
    public void addCourseDuplicateJaverianaId() throws Exception {
        String addRequest = """
                {
                    "courseId": 1,
                    "javerianaId": 12345,
                    "coordinatorId": 4,
                    "name": "Duplicate Course",
                    "faculty": "Engineering",
                    "department": "Computer Science",
                    "program": "Software Engineering",
                    "semester": 6
                }
                """;

        mockMvc.perform(post("/course/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").value("Course with javeriana ID 12345 already exists"));
    }

    @Test
    @Order(14)
    public void addCourseInvalidCoordinator() throws Exception {
        String addRequest = """
                {
                    "courseId": 1,
                    "javerianaId": 67891,
                    "coordinatorId": 999,
                    "name": "Invalid Coordinator Course",
                    "faculty": "Engineering",
                    "department": "Computer Science",
                    "program": "Software Engineering",
                    "semester": 6
                }
                """;

        mockMvc.perform(post("/course/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").value("Coordinator with ID 999 not found"));
    }

    @Test
    @Order(15)
    public void updateCourseDuplicateJaverianaId() throws Exception {
        String updateRequest = """
                {
                    "courseId": 3,
                    "javerianaId": 100002,
                    "name": "Duplicate Javeriana ID",
                    "coordinatorId": 4,
                    "classes": [],
                    "faculty": "Engineering",
                    "department": "Computer Science",
                    "program": "Software Engineering",
                    "semester": 8
                }
                """;

        mockMvc.perform(put("/course/update/3")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").value("Javeriana ID 100002 already exists"));
    }

    @Test
    @Order(16)
    public void updateCourseInvalidCoordinator() throws Exception {
        String updateRequest = """
                {
                    "courseId": 1,
                    "javerianaId": 67892,
                    "name": "Invalid Coordinator Update",
                    "coordinatorId": 999,
                    "faculty": "Engineering",
                    "department": "Computer Science",
                    "program": "Software Engineering",
                    "semester": 8
                }
                """;

        mockMvc.perform(put("/course/update/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").value("Coordinator with ID 999 not found"));
    }
}
