package co.edu.javeriana.lms.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RubricTemplateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private String token;

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
    void setUp() throws Exception {
        token = getAuthTokenString();
    }

    @Test
    void createRubricTemplate_WithValidData_ShouldReturnCreated() throws Exception {

        String dto = """
                {
                    "title": "Nueva Plantilla",
                    "courses": [7],
                    "columns": [
                        {
                            "title": "No aprobado",
                            "scoringScale": {
                                "lowerValue": 0,
                                "upperValue": 3
                            }
                        },
                        {
                            "title": "Aprobado",
                            "scoringScale": {
                                "lowerValue": 3,
                                "upperValue": 5
                            }
                        }
                    ],
                    "criteria": [
                        {
                            "name": "Criterio 1",
                            "weight": 50,
                            "scoringScaleDescription": [
                                "Descripción",
                                "Descripción"
                            ]
                        },
                        {
                            "name": "Criterio 2",
                            "weight": 50,
                            "scoringScaleDescription": [
                                "Descripción",
                                "Descripción"
                            ]
                        }
                    ],
                    "archived": false
                }""";

        mockMvc.perform(post("/rubric/template")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(200)) // Nota: Debería ser 201
                .andExpect(jsonPath("$.data.title").value("Nueva Plantilla"));
    }

    @Test
    void getRubricTemplateById_WhenExists_ShouldReturnTemplate() throws Exception {
        Long existingId = 1L;

        mockMvc.perform(get("/rubric/template/{id}", existingId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.rubricTemplateId").exists());
    }

    @Test
    void updateRubricTemplate_WithValidData_ShouldReturnUpdated() throws Exception {
        Long existingId = 1L;

                String dto = """
                {
                    "title": "Plantilla Actualizada",
                    "courses": [7],
                    "columns": [
                        {
                            "title": "No aprobado",
                            "scoringScale": {
                                "lowerValue": 0,
                                "upperValue": 3
                            }
                        },
                        {
                            "title": "Aprobado",
                            "scoringScale": {
                                "lowerValue": 3,
                                "upperValue": 5
                            }
                        }
                    ],
                    "criteria": [
                        {
                            "name": "Criterio 1",
                            "weight": 50,
                            "scoringScaleDescription": [
                                "Descripción",
                                "Descripción"
                            ]
                        },
                        {
                            "name": "Criterio 2",
                            "weight": 50,
                            "scoringScaleDescription": [
                                "Descripción",
                                "Descripción"
                            ]
                        }
                    ],
                    "archived": false
                }""";

        mockMvc.perform(put("/rubric/template/{id}", existingId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.title").value("Plantilla Actualizada"));
    }

    @Test
    void archiveRubricTemplate_ShouldChangeStatus() throws Exception {
        Long existingId = 1L;

        mockMvc.perform(put("/rubric/template/archive/{id}", existingId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.archived").value(true));
    }

    @Test
    void deleteRubricTemplate_WhenNoDependencies_ShouldSucceed() throws Exception {
        String dto = """
                {
                    "title": "Nueva Plantilla",
                    "courses": [7],
                    "columns": [
                        {
                            "title": "No aprobado",
                            "scoringScale": {
                                "lowerValue": 0,
                                "upperValue": 3
                            }
                        },
                        {
                            "title": "Aprobado",
                            "scoringScale": {
                                "lowerValue": 3,
                                "upperValue": 5
                            }
                        }
                    ],
                    "criteria": [
                        {
                            "name": "Criterio 1",
                            "weight": 50,
                            "scoringScaleDescription": [
                                "Descripción",
                                "Descripción"
                            ]
                        },
                        {
                            "name": "Criterio 2",
                            "weight": 50,
                            "scoringScaleDescription": [
                                "Descripción",
                                "Descripción"
                            ]
                        }
                    ],
                    "archived": false
                }""";

        String response = mockMvc.perform(post("/rubric/template")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(dto))
                .andReturn().getResponse().getContentAsString();

        Long newId = objectMapper.readTree(response).path("data").path("rubricTemplateId").asLong();

        System.out.println("ID de la plantilla creada: " + response);

        mockMvc.perform(delete("/rubric/template/{id}", newId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void getRecommendedTemplates_ShouldReturnList() throws Exception {
        Long practiceId = 1L; // Asume que existe en tus datos de prueba

        mockMvc.perform(get("/rubric/template/recommended/{idPractice}", practiceId)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

}