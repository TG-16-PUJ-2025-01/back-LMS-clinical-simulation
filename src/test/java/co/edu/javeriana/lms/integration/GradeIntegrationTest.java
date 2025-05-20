package co.edu.javeriana.lms.integration;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import co.edu.javeriana.lms.grades.dtos.PracticePercentageDto;
import co.edu.javeriana.lms.grades.dtos.PracticesPercentagesDto;
import co.edu.javeriana.lms.grades.dtos.StudentGradeDto;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.videos.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class GradeIntegrationTest {

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
    public void testGetClassGrades() throws Exception {
        String response = mockMvc.perform(get("/grade/class/1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ApiResponseDto<List<StudentGradeDto>> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<List<StudentGradeDto>>>() {}
        );
        
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getData());
        assert apiResponse.getData().size() > 0;
    }

    @Test
    public void testGetStudentGradeByToken() throws Exception {
        String response = mockMvc.perform(get("/grade/student/1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ApiResponseDto<StudentGradeDto> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<StudentGradeDto>>() {}
        );
        
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getData());
    }

    @Test
    public void testUpdateClassGradePercentages() throws Exception {
        PracticesPercentagesDto classGradePercentagesDto = new PracticesPercentagesDto(List.of(
            new PracticePercentageDto(1L, 0.5f),
            new PracticePercentageDto(2L, 0.5f)
        ));

        String requestBody = objectMapper.writeValueAsString(classGradePercentagesDto);
        
        mockMvc.perform(put("/grade/percentages")
                .header("Authorization", token)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPracticesPercentagesByClass() throws Exception {
        String response = mockMvc.perform(get("/grade/class/1/percentages")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ApiResponseDto<List<PracticePercentageDto>> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<List<PracticePercentageDto>>>() {}
        );
        
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getData());
        assert apiResponse.getData().size() > 0;
    }

}
