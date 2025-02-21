package co.edu.javeriana.lms;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllRooms_Integration() throws Exception {
        mockMvc.perform(get("/rooms/all?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    public void testAddRoom_Integration_Success() throws Exception {
        // Send a JSON with valid data to create a new room
        String validRoomJson = "{\"name\":\"RoomX\", \"type\": {\"name\":\"Cirugia\"}}";
        mockMvc.perform(post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Room created successfully"));
    }

    @Test
    public void testAddRoom_InvalidData() throws Exception {
        // Send a JSON with an empty name, which is invalid
        String invalidRoomJson = "{\"name\":\"\", \"type\": {\"name\":\"Cirugia\"}}";

        mockMvc.perform(post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRoomJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    public void testAddRoom_Integration_NameConflict() throws Exception {
        // Primero, se crea una sala con un nombre determinado
        String validRoomJson = "{\"name\":\"RoomX\", \"type\": {\"name\":\"Cirugia\"}}";
        mockMvc.perform(post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isCreated());

        // Al intentar crear otra sala con el mismo nombre, se debe lanzar el error por
        // conflicto
        mockMvc.perform(post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRoomJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El nombre de la sala ya existe"));
    }

    @Test
    public void testAddRoom_InvalidData_Integration() throws Exception {
        // Send a JSON with invalid data (empty name)
        String invalidRoomJson = "{\"name\":\"\", \"type\": {\"name\":\"Cirugia\"}}";
        mockMvc.perform(post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRoomJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}