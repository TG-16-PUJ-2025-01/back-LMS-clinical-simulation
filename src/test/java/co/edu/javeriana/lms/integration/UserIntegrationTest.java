package co.edu.javeriana.lms.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

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
import co.edu.javeriana.lms.accounts.dtos.RegisterUserDto;
import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.videos.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class UserIntegrationTest {
    
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
    public void testGetAllUsers() throws Exception {
        String response = mockMvc.perform(get("/user/all")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ApiResponseDto<List<User>> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<List<User>>>() {}
        );
        
        assertNotNull(apiResponse.getData());
        assert apiResponse.getData().size() > 0;
        assertNotNull(apiResponse.getMetadata());
    }

    @Test
    public void testAddUser() throws Exception {
        RegisterUserDto user = RegisterUserDto.builder()
                .institutionalId("12345678910")
                .email("test@gmail.com")
                .name("Test User")
                .lastName("User")
                .roles(Set.of(Role.ESTUDIANTE.name()))
                .build();
        String userRequest = objectMapper.writeValueAsString(user);

        String response = mockMvc.perform(post("/user/add")
                .header("Authorization", token)
                .contentType("application/json")
                .content(userRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ApiResponseDto<User> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<User>>() {}
        );

        assertNotNull(apiResponse.getData());
        assertEquals(user.getInstitutionalId(), apiResponse.getData().getInstitutionalId());
        assertEquals(user.getEmail(), apiResponse.getData().getEmail());
        assertEquals(user.getName(), apiResponse.getData().getName());  
    }

    @Test
    public void testAddUserExcel() throws Exception {
        RegisterUserDto user = RegisterUserDto.builder()
                .institutionalId("12345678910")
                .email("test@gmail.com")
                .name("Test User")
                .lastName("User")
                .roles(Set.of(Role.ESTUDIANTE.name()))
                .build();
        String userRequest = objectMapper.writeValueAsString(user);

        String response = mockMvc.perform(post("/user/add/excel")
                .header("Authorization", token)
                .contentType("application/json")
                .content(userRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ApiResponseDto<User> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<User>>() {}
        );

        assertNotNull(apiResponse.getData());
        assertEquals(user.getInstitutionalId(), apiResponse.getData().getInstitutionalId());
        assertEquals(user.getEmail(), apiResponse.getData().getEmail());
        assertEquals(user.getName(), apiResponse.getData().getName());  
    }

    @Test
    public void testGetUserById() throws Exception {
        String response = mockMvc.perform(get("/user/1")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        ApiResponseDto<User> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<User>>() {}
        );

        assertNotNull(apiResponse.getData());
        assertEquals(1L, apiResponse.getData().getId().longValue());
    }

    @Test
    public void testUpdateUserById() throws Exception {
        RegisterUserDto user = RegisterUserDto.builder()
                .institutionalId("12345678911")
                .email("test2@gmail.com")
                .name("Test2 User")
                .lastName("User2")
                .roles(Set.of(Role.ESTUDIANTE.name(), Role.PROFESOR.name()))
                .build();
        String userRequest = objectMapper.writeValueAsString(user);

        String response = mockMvc.perform(put("/user/update/1")
                .header("Authorization", token)
                .contentType("application/json")
                .content(userRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ApiResponseDto<User> apiResponse = objectMapper.readValue(
            response,
            new TypeReference<ApiResponseDto<User>>() {}
        );

        assertNotNull(apiResponse.getData());
        assertEquals(user.getInstitutionalId(), apiResponse.getData().getInstitutionalId());
        assertEquals(user.getEmail(), apiResponse.getData().getEmail());
        assertEquals(user.getName(), apiResponse.getData().getName());
        assertEquals(user.getLastName(), apiResponse.getData().getLastName());
        assertEquals(user.getRoles().size(), apiResponse.getData().getRoles().size());
        assertEquals(user.getRoles().stream().map(Role::valueOf).collect(java.util.stream.Collectors.toSet()), apiResponse.getData().getRoles());
    }

    @Test
    public void testDeleteUserById() throws Exception {
        mockMvc.perform(delete("/user/delete/26")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

}
