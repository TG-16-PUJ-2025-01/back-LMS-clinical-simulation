package co.edu.javeriana.lms.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
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

import co.edu.javeriana.lms.grades.dtos.EvaluatedCriteriaDto;
import co.edu.javeriana.lms.grades.dtos.RubricDto;
import co.edu.javeriana.lms.practices.dtos.CreateSimulationRequestDto;
import co.edu.javeriana.lms.practices.dtos.SimulationByTimeSlotDto;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class SimulationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    private String token;

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private String getTokenString() throws Exception {
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

    @BeforeEach
    public void setup() throws Exception {
        token = getTokenString();
    }

    @Test
    public void testFindAllSimulations() throws Exception {
        mockMvc.perform(get("/simulation/all")
                .header("Authorization", token)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", is(not(empty()))))
                .andExpect(jsonPath("$.metadata.total", is(greaterThan(0))));
    }

    @Test
    public void testFindSimulationById() throws Exception {
        Long simulationId = 1L; // ID de una simulación existente en tus datos de prueba

        mockMvc.perform(get("/simulation/{id}", simulationId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.simulationId", is(simulationId.intValue())));
    }

    @Test
    public void testFindSimulationById_NotFound() throws Exception {
        Long nonExistentId = 9999L;

        mockMvc.perform(get("/simulation/{id}", nonExistentId)
                .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", is("Simulation not found with id: " + nonExistentId)));
    }

    @Test
    public void testFindSimulationsByPracticeId() throws Exception {
        Long practiceId = 1L; // ID de práctica existente

        mockMvc.perform(get("/simulation/practice/{practiceId}", practiceId)
                .header("Authorization", token)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "simulationId")
                .param("asc", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", is(not(empty()))))
                .andExpect(jsonPath("$.metadata.total", is(greaterThan(0))));
    }

    @Test
    public void testFindSimulationsByPracticeId_WithGroupNumber() throws Exception {
        Long practiceId = 1L;
        Integer groupNumber = 1;

        mockMvc.perform(get("/simulation/practice/{practiceId}", practiceId)
                .header("Authorization", token)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "simulationId")
                .param("asc", "true")
                .param("groupNumber", groupNumber.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data[0].groupNumber", is(groupNumber)));
    }

    @Test
    public void testUpdateSimulationRubric() throws Exception {
        Long simulationId = 1L;
        RubricDto rubricDto = RubricDto.builder()
                .evaluatedCriterias(List.of(
                        EvaluatedCriteriaDto.builder()
                                .comment("Buen desempeño")
                                .score(4.0f)
                                .build()))
                .total(EvaluatedCriteriaDto.builder()
                        .comment("Total")
                        .score(4.5f)
                        .build())
                .build();

        mockMvc.perform(put("/simulation/{id}/rubric", simulationId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rubricDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", is(notNullValue())));
    }

    @Test
    public void testPublishGrade() throws Exception {
        Long simulationId = 1L; // ID de simulación con rúbrica

        mockMvc.perform(put("/simulation/{id}/publish", simulationId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.gradeStatus", is("REGISTERED")));
    }

    @Test
    public void testPublishGrade_WithoutRubric() throws Exception {
        Long simulationId = 3L; // ID de simulación sin rúbrica (según tus datos de prueba)

        mockMvc.perform(put("/simulation/{id}/publish", simulationId)
                .header("Authorization", token))
                .andExpect(status().isOk()) // Ajusta según cómo maneje tu controlador los errores
                .andExpect(jsonPath("$.status", is(200)));
    }

    @Test
    public void testAddSimulation() throws Exception {
        CreateSimulationRequestDto simulationRequestDto = new CreateSimulationRequestDto(List.of(
                SimulationByTimeSlotDto.builder()
                        .practiceId(4L)
                        .roomIds(List.of(1L, 2L))
                        .startDateTime(new Date())
                        .endDateTime(new Date(System.currentTimeMillis() + 900000)) // 15 mins
                                                                                    // later
                        .build(),
                SimulationByTimeSlotDto.builder()
                        .practiceId(4L)
                        .roomIds(List.of(1L, 2L))
                        .startDateTime(new Date(System.currentTimeMillis() + 900000))
                        .endDateTime(new Date(System.currentTimeMillis() + 1800000)) // 30 mins
                                                                                     // later
                        .build(),
                SimulationByTimeSlotDto.builder()
                        .practiceId(4L)
                        .roomIds(List.of(1L, 2L))
                        .startDateTime(new Date(System.currentTimeMillis() + 1800000))
                        .endDateTime(new Date(System.currentTimeMillis() + 2700000)) // 45 mins
                                                                                     // later
                        .build(),
                SimulationByTimeSlotDto.builder()
                        .practiceId(4L)
                        .roomIds(List.of(1L, 2L))
                        .startDateTime(new Date(System.currentTimeMillis() + 2700000))
                        .endDateTime(new Date(System.currentTimeMillis() + 3600000)) // 1 h
                                                                                     // later
                        .build(),
                SimulationByTimeSlotDto.builder()
                        .practiceId(4L)
                        .roomIds(List.of(1L, 2L))
                        .startDateTime(new Date(System.currentTimeMillis() + 3600000))
                        .endDateTime(new Date(System.currentTimeMillis() + 4500000)) // 1 h 15
                                                                                     // mins
                                                                                     // later
                        .build()));

        String simulationRequest = objectMapper.writeValueAsString(simulationRequestDto);

        String response = mockMvc.perform(post("/simulation")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        assert jsonNode.get("data").size() == 5;
    }

    @Test
    public void testUpdateSimulation() throws Exception {
        Long simulationId = 1L; // ID de simulación existente

        SimulationByTimeSlotDto simulationRequestDto = SimulationByTimeSlotDto.builder()
                .practiceId(4L)
                .roomIds(List.of(1L, 2L))
                .startDateTime(new Date())
                .endDateTime(new Date(System.currentTimeMillis() + 900000)) // 15 mins
                                                                            // later
                .build();

        String simulationRequest = objectMapper.writeValueAsString(simulationRequestDto);

        mockMvc.perform(put("/simulation/{id}", simulationId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.simulationId", is(simulationId.intValue())));
    }

    @Test
    public void testDeleteSimulation() throws Exception {
        Long simulationId = 4L; // ID de simulación existente

        mockMvc.perform(delete("/simulation/{id}", simulationId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));
    }

    @Test
    public void testFindSimulationsSchedule() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String date = LocalDate.now().minusWeeks(2).with(DayOfWeek.MONDAY).format(formatter);

        mockMvc.perform(get("/simulation/schedule")
                .header("Authorization", token)
                .param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", is(not(empty()))));
    }
}
