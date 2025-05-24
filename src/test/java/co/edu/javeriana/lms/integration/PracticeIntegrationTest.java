package co.edu.javeriana.lms.integration;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.javeriana.lms.accounts.dtos.LoginDto;
import co.edu.javeriana.lms.accounts.dtos.LoginResponseDto;
import co.edu.javeriana.lms.practices.dtos.PracticeDto;
import co.edu.javeriana.lms.practices.models.PracticeType;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.videos.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class PracticeIntegrationTest {
    
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
    private String email = "superadmin@gmail.com";
    private String password = "superadmin";

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private LoginResponseDto getAuthTokenString() throws Exception {
        LoginDto loginDto = new LoginDto(email, password);
        String loginRequest = objectMapper.writeValueAsString(loginDto);

        String response = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ApiResponseDto<LoginResponseDto> loginResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<LoginResponseDto>>() {}
        );
        
        return loginResponse.getData();
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
    public void setUp() throws Exception {
        token = "Bearer " + getAuthTokenString().getToken();
    }
    
    @Test
    public void testFindAll() throws Exception {
        mockMvc.perform(get("/practice/all")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", is(not(empty()))));
    }

    @Test
    public void testFindById() throws Exception {
        mockMvc.perform(get("/practice/1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    public void testSave() throws Exception {
        PracticeDto practiceDto = PracticeDto.builder()
                .name("Test Practice")
                .description("Test Description")
                .gradeable(true)
                .type(PracticeType.GRUPAL)
                .simulationDuration(30)
                .numberOfGroups(2)
                .maxStudentsGroup(5)
                .build();
        String practiceJson = objectMapper.writeValueAsString(practiceDto);

        mockMvc.perform(post("/practice/add/1")
                .header("Authorization", token)
                .contentType("application/json")
                .content(practiceJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteById() throws Exception {
        mockMvc.perform(get("/practice/4")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.id", is(4)));

        mockMvc.perform(delete("/practice/4")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Practice deleted successfully")));

        mockMvc.perform(get("/practice/4")
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate() throws Exception {
        PracticeDto practiceDto = PracticeDto.builder()
                .name("Updated Practice")
                .description("Updated Description")
                .gradeable(true)
                .type(PracticeType.GRUPAL)
                .simulationDuration(30)
                .numberOfGroups(2)
                .maxStudentsGroup(5)
                .build();
        String practiceJson = objectMapper.writeValueAsString(practiceDto);

        mockMvc.perform(put("/practice/1")
                .header("Authorization", token)
                .contentType("application/json")
                .content(practiceJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindByClassId() throws Exception {
        mockMvc.perform(get("/practice/class/1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", is(not(empty()))));
    }

    @Test
    public void testGetEnrolledSimulation() throws Exception {
        mockMvc.perform(get("/practice/1/enrolled")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", is(not(empty()))));
    }
}
