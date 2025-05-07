package co.edu.javeriana.lms.services;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.accounts.services.UserService;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    static User mockUser1;
    static User mockUser2;

    @BeforeAll
    public static void setUpAll() {
        mockUser1 = User.builder()
                .id(1L)
                .email("mockEmail1")
                .password("mockPassword1")
                .roles(Set.of(Role.ADMIN, Role.COORDINADOR))
                .preferredRole(Role.ADMIN)
                .name("Mock User 1")
                .lastName("Mock Last Name 1")
                .institutionalId("123456")
                .build();

        mockUser2 = User.builder()
                .id(1L)
                .email("mockEmail2")
                .password("mockPassword2")
                .roles(Set.of(Role.ADMIN, Role.COORDINADOR))
                .preferredRole(Role.ADMIN)
                .name("Mock User 2")
                .lastName("Mock Last Name 2")
                .institutionalId("654321")
                .build();
    }

    @Test
    public void testGetAllUsers() {

        when(userRepository.findAllFiltered("", PageRequest.of(0, 10, Sort.by("email").ascending())))
        .thenReturn(new PageImpl<>(List.of(mockUser1, mockUser2)));

        Page<User> users = userService.getAllUsers("", 0, 10, "email", true);

        assert users.getTotalElements() == 2;
        assert users.getContent().get(0).getEmail().equals("mockEmail1");
        assert users.getContent().get(1).getEmail().equals("mockEmail2");
        assert users.getTotalPages() == 1;
        assert users.getSize() == 2;
        assert users.getNumber() == 0;

    }
}
