package co.edu.javeriana.lms.integration;

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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class ClassIntegrationTest {

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
    public void findAllClasses() throws Exception {
        mockMvc.perform(get("/class/all?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Classes retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total", is(8)))
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.totalPages", is(2)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(2)
    public void getAllClassMembers() throws Exception {
        mockMvc.perform(get("/class/1/member/all?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total").isNumber())
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(3)
    public void getAllClassStudentsMembers() throws Exception {
        mockMvc.perform(get("/class/1/member/students?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total").isNumber())
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(4)
    public void getAllClassProfessorsMembers() throws Exception {
        mockMvc.perform(get("/class/1/member/professors?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total").isNumber())
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(5)
    public void getAllClassMembersNotInClass() throws Exception {
        mockMvc.perform(get("/class/1/member/all/outside?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total").isNumber())
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(6)
    public void getAllClassStudentsMembersNotInClass() throws Exception {
        mockMvc.perform(get("/class/1/member/students/outside?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.metadata.total").isNumber())
                .andExpect(jsonPath("$.metadata.size", is(5)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(7)
    public void getAllClassProfessorsMembersNotInClass() throws Exception {
        mockMvc.perform(get("/class/3/member/professors/outside?page=0&size=5")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andExpect(jsonPath("$.metadata.total").isNumber())
                .andExpect(jsonPath("$.metadata.size", is(4)))
                .andExpect(jsonPath("$.metadata.page", is(0)));
    }

    @Test
    @Order(8)
    public void getClassById() throws Exception {
        mockMvc.perform(get("/class/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.classId").value(1));
    }

    @Test
    @Order(9)
    public void deleteClassById() throws Exception {
        mockMvc.perform(delete("/class/delete/7")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Clase deleted successfully."));
    }

    @Test
    @Order(10)
    public void updateClass() throws Exception {
        String updateRequest = """
                {
                    "javerianaId": 12345,
                    "professorsIds": [1, 2],
                    "courseId": 3,
                    "period": "2025-10",
                    "numberOfParticipants": 30
                }
                """;

        mockMvc.perform(put("/class/update/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Class updated successfully."))
                .andExpect(jsonPath("$.data.numberOfParticipants").value(30));
    }

    @Test
    @Order(11)
    public void addClass() throws Exception {
        String addRequest = """
                {
                    "javerianaId": 67890,
                    "professorsIds": [1, 2],
                    "courseId": 3,
                    "period": "2025-20",
                    "numberOfParticipants": 25
                }
                """;

        mockMvc.perform(post("/class/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Class added successfully."))
                .andExpect(jsonPath("$.data.javerianaId").value(67890));
    }

    @Test
    @Order(12)
    public void getClassesByProfessor() throws Exception {
        mockMvc.perform(get("/class/all/professor?year=2025&period=10")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Classes retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].period").value("2025-10"));
    }

    @Test
    @Order(13)
    public void getClassesByStudent() throws Exception {
        mockMvc.perform(get("/class/all/student?year=2025&period=10")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Classes retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(6)))
                .andExpect(jsonPath("$.data[0].period").value("2025-10"));
    }

    @Test
    @Order(14)
    public void deleteClassMemberById() throws Exception {
        mockMvc.perform(delete("/class/delete/1/member/2")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Member class deleted successfully."));
    }

    @Test
    @Order(15)
    public void updateClassMembers() throws Exception {
        String updateMembersRequest = """
                [
                    {
                        "id": 1,
                        "email": "john.doe@example.com",
                        "name": "John",
                        "lastName": "Doe",
                        "institutionalId": "00012345",
                        "roles": ["ESTUDIANTE"]
                    },
                    {
                        "id": 2,
                        "email": "jane.smith@example.com",
                        "name": "Jane",
                        "lastName": "Smith",
                        "institutionalId": "00012346",
                        "roles": ["ESTUDIANTE"]
                    }
                ]
                """;

        mockMvc.perform(put("/class/update/1/members")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateMembersRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Class updated successfully."))
                .andExpect(jsonPath("$.data.professors", hasSize(4)));
    }

    // TODO: Fix
    @Test
    @Order(16)
    public void updateClassProfessorMember() throws Exception {
        String updateMembersRequest = """
                [
                    {
                        "id": 1,
                        "email": "john.doe@example.com",
                        "name": "John",
                        "lastName": "Doe",
                        "institutionalId": "00000010001",
                        "roles": ["PROFESOR"]
                    },
                ]
                """;

        mockMvc.perform(put("/class/update/1/members/professor/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateMembersRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Class updated successfully."));
    }

    // TODO: Fix
    @Test
    @Order(17)
    public void updateClassStudentMember() throws Exception {
        mockMvc.perform(put("/class/update/1/members/student/4")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Class updated successfully."));
    }
}
