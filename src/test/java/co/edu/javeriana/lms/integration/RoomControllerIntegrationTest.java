package co.edu.javeriana.lms.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    static final RoomType mockRoomType = RoomType.builder().name("Cirugia").build();
    static final Room mockRoom = Room.builder().name("RoomA").type(mockRoomType).capacity(10).build();
    private static String jwtToken;

    @BeforeAll
    static void BeforeAll(@Autowired RoomRepository roomRepository, @Autowired RoomTypeRepository roomTypeRepository,
            @Autowired MockMvc mockMvc) throws Exception {
        postgres.start();
        roomTypeRepository.save(mockRoomType);
        roomRepository.save(mockRoom);

        // Authenticate and get JWT token
        String loginJson = "{\"email\":\"andresgarciam@javeriana.edu.co\", \"password\":\"Peter2010?\"}";
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        jwtToken = result.getResponse().getContentAsString();
    }

    @AfterAll
    static void AfterAll() {
        postgres.stop();
    }

    @Test
    public void testGetAllRooms_Integration() throws Exception {
        mockMvc.perform(get("/room/all?page=0&size=10")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    public void testAddRoom_Integration_Success() throws Exception {
        // Send a JSON with valid data to create a new room
        String validRoomJson = "{\"name\":\"Room2X\", \"capacity\":\"10\", \"type\": {\"name\":\"UCI2\"}}";
        mockMvc.perform(post("/room/add")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Room created successfully"));
    }

    @Test
    public void testAddRoom_InvalidData() throws Exception {
        // Send a JSON with an empty name, which is invalid
        String invalidRoomJson = "{\"name\":\"\", \"capacity\":\"10\", \"type\": {\"name\":\"Cirugia\"}}";

        mockMvc.perform(post("/room/add")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRoomJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    public void testAddRoom_Integration_NameConflict() throws Exception {
        // First, create a room with a specific name
        String validRoomJson = "{\"name\":\"RoomX\", \"capacity\":\"10\", \"type\": {\"name\":\"Urgencias\"}}";
        mockMvc.perform(post("/room/add")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isCreated());

        // Then, try to create another room with the same name, it should fail
        mockMvc.perform(post("/room/add")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Invalid data or duplicate entry"));
    }

    @Test
    public void testAddRoom_InvalidData_Integration() throws Exception {
        // Send a JSON with invalid data (empty name)
        String invalidRoomJson = "{\"name\":\"\", \"type\": {\"name\":\"Cirugia\"}}";
        mockMvc.perform(post("/room/add")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRoomJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}