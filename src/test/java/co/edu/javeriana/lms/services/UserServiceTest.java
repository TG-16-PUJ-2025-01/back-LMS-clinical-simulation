package co.edu.javeriana.lms.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.accounts.services.EmailService;
import co.edu.javeriana.lms.accounts.services.UserService;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

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

    @Test
    public void testAddUser() {
        when(userRepository.save(mockUser1)).thenReturn(mockUser1);
        when(userRepository.existsByEmail(mockUser1.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        

        User savedUser = userService.addUser(mockUser1);

        assert savedUser.getEmail().equals("mockEmail1");
        assert savedUser.getPassword().equals("encodedPassword");
        assert savedUser.getRoles().contains(Role.ADMIN);
        assert savedUser.getPreferredRole().equals(Role.ADMIN);
        assert savedUser.getName().equals("Mock User 1");
        assert savedUser.getLastName().equals("Mock Last Name 1");
        assert savedUser.getInstitutionalId().equals("123456");
    }

    @Test
    public void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));

        User foundUser = userService.findById(1L);

        assert foundUser.getEmail().equals(mockUser1.getEmail());
        assert foundUser.getPassword().equals(mockUser1.getPassword());
        assert foundUser.getRoles().contains(Role.ADMIN);
        assert foundUser.getPreferredRole().equals(Role.ADMIN);
        assert foundUser.getName().equals(mockUser1.getName());
        assert foundUser.getLastName().equals(mockUser1.getLastName());
        assert foundUser.getInstitutionalId().equals(mockUser1.getInstitutionalId());
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            userService.findById(1L);
        } catch (Exception e) {
            assert e.getMessage().equals("User not found with id: 1");
        }
    }

    @Test
    public void testUpdateUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));
        when(userRepository.save(mockUser1)).thenReturn(mockUser1);

        User updatedUser = userService.updateUserById(1L, mockUser2);

        assert updatedUser.getEmail().equals(mockUser2.getEmail());
        assert updatedUser.getPassword().equals(mockUser1.getPassword());
        assert updatedUser.getRoles().contains(Role.ADMIN);
        assert updatedUser.getPreferredRole().equals(Role.ADMIN);
        assert updatedUser.getName().equals(mockUser2.getName());
        assert updatedUser.getLastName().equals(mockUser2.getLastName());
        assert updatedUser.getInstitutionalId().equals(mockUser2.getInstitutionalId());
    }

    @Test
    public void testUpdateUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            userService.updateUserById(1L, mockUser2);
        } catch (Exception e) {
            assert e.getMessage().equals("User not found with id: 1");
        }
    }

    @Test
    public void testDeleteById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void testDeleteByIdNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        try {
            userService.deleteById(1L);
        } catch (Exception e) {
            assert e.getMessage().equals("User not found with id: 1");
        }
    }
}
