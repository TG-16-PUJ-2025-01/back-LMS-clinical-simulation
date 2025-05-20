package co.edu.javeriana.lms.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class RoomControllerIntegrationTest {

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
    static void AfterAll() {
        postgres.stop();
    }

    @BeforeEach
    public void setup() throws Exception {
        token = getAuthTokenString();
        System.out.println("Token: " + token);
    }

    @Test
    @Order(1)
    public void testGetAllRooms_Integration() throws Exception {
        mockMvc.perform(get("/room/all?page=0&size=10")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rooms retrieved successfully"));
    }

    @Test
    @Order(2)
    public void testUpdateRoom_Integration_Success() throws Exception {
        String updatedRoomJson = "{\"name\":\"UpdatedRoom\", \"capacity\":\"15\", \"ip\":\"10.0.0.1\", \"typeId\": \"1\"}";
        mockMvc.perform(put("/room/1")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedRoomJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updatedroom"))
                .andExpect(jsonPath("$.data.capacity").value(15));
    }

    @Test
    @Order(3)
    public void testAddRoom_Integration_Success() throws Exception {
        String validRoomJson = "{\"name\":\"Room2X\", \"capacity\":\"10\", \"ip\":\"10.0.0.2\", \"typeId\": \"1\"}";
        mockMvc.perform(post("/room")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room created successfully"));
    }

    @Test
    @Order(4)
    public void testAddRoom_InvalidData() throws Exception {
        String invalidRoomJson = "{\"name\":\"\", \"capacity\":\"10\", \"ip\":\"10.0.0.3\", \"typeId\": \"1\"}";
        mockMvc.perform(post("/room")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRoomJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @Order(5)
    public void testAddRoom_Integration_NameConflict() throws Exception {
        String validRoomJson = "{\"name\":\"RoomX\", \"capacity\":\"10\", \"ip\":\"10.0.0.4\", \"typeId\": \"1\"}";
        mockMvc.perform(post("/room")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isOk());

        // Then try to create another room with the same name and data
        mockMvc.perform(post("/room")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Invalid data or duplicate entry"));
    }

    @Test
    @Order(6)
    public void testGetRoomById_Integration_Success() throws Exception {
        mockMvc.perform(get("/room/1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room retrieved successfully"))
                .andExpect(jsonPath("$.data.name").value("Consultorio 1"));
    }

    @Test
    @Order(7)
    public void testGetRoomById_Integration_NotFound() throws Exception {
        mockMvc.perform(get("/room/9999")
                .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"));
    }

    @Test
    @Order(8)
    public void testGetRoomTypes_Integration() throws Exception {
        mockMvc.perform(get("/room/types")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room types retrieved successfully"))
                .andExpect(jsonPath("$.data[0].name").value("Consultorio"));
    }

    @Test
    @Order(9)
    public void testDeleteRoomById_Integration_Success() throws Exception {
        mockMvc.perform(delete("/room/3")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room deleted successfully"));
    }

    @Test
    @Order(10)
    public void testDeleteRoomById_Integration_NotFound() throws Exception {
        mockMvc.perform(delete("/room/9999")
                .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"));
    }

    @Test
    @Order(11)
    public void testUpdateRoom_Integration_NotFound() throws Exception {
        String updatedRoomJson = "{\"name\":\"UpdatedRoom\", \"capacity\":\"15\", \"ip\":\"10.0.0.5\", \"typeId\": \"1\"}";
        mockMvc.perform(put("/room/9999")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedRoomJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not found"));
    }

    @Test
    @Order(12)
    public void testAddRoomType_Integration_Success() throws Exception {
        String newRoomTypeJson = "{\"name\":\"NewRoomType\"}";
        mockMvc.perform(post("/room/type")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRoomTypeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room type created successfully"))
                .andExpect(jsonPath("$.data.name").value("NewRoomType"));
    }

    @Test
    @Order(13)
    public void testAddRoomType_Integration_InvalidData() throws Exception {
        String invalidRoomTypeJson = "{\"name\":\"\"}";
        mockMvc.perform(post("/room/type")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRoomTypeJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Invalid data or duplicate entry"));
    }
}