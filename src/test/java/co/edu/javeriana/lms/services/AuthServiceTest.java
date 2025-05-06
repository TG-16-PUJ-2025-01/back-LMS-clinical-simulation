package co.edu.javeriana.lms.services;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.accounts.dtos.LoginResponseDto;
import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.accounts.services.EmailService;
import co.edu.javeriana.lms.shared.services.JwtService;

@SpringBootTest
@ActiveProfiles("test")
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    private static String mockEmail = "mock@email.com";
    private static String mockPassword = "mockPassword";
    private static String mockNewPassword = "newMockPassword";
    private static String mockEncodedPassword = "encodedMockPassword";
    private static User mockUser;
    private static String mockToken = "mockToken";

    @BeforeAll
    public static void setUpAll() {
        mockUser = User.builder()
                .id(1L)
                .email(mockEmail)
                .password(mockEncodedPassword)
                .roles(Set.of(Role.ADMIN, Role.COORDINADOR))
                .preferredRole(Role.ADMIN)
                .name("Mock User")
                .lastName("Mock Last Name")
                .institutionalId("123456")
                .build();
    }

    @Test
    public void testLogin() {
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(mockPassword, mockUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn(mockToken);

        LoginResponseDto res = authService.login(mockEmail, mockPassword);

        assert res.getToken().equals(mockToken);
        assert res.getRoles().contains(Role.ADMIN);
        assert res.getRoles().contains(Role.COORDINADOR);
        assert res.getPreferredRole().equals(Role.ADMIN);
        verify(userRepository).findByEmail(mockEmail);
        verify(passwordEncoder).matches(mockPassword, mockUser.getPassword());
        verify(jwtService).generateToken(mockUser);
    }

    @Test
    public void testLoginUserNotFound() {
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(mockEmail, mockPassword));
    }

    @Test
    public void testLoginInvalidCredentials() {
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(mockPassword, mockUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(mockEmail, mockPassword));
    }

    @Test
    public void testChangePassword() {
        String mockSubject = "Cambio de contraseña LMS";
        String mockBody = "Hola " + mockUser.getEmail() + ",\n\nTu contraseña ha sido cambiada con éxito.\n" +
                "Si no fuiste tú, por favor, contacta al administrador.";

        when(jwtService.extractUserName(mockToken)).thenReturn(mockEmail);
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(mockPassword, mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(mockNewPassword)).thenReturn(mockEncodedPassword);
        when(jwtService.generateToken(mockUser)).thenReturn(mockToken);

        String token = authService.changePassword(mockToken, mockPassword, mockNewPassword);

        assert token.equals(mockToken);
        assert mockUser.getPassword().equals(mockEncodedPassword);
        verify(userRepository).findByEmail(mockEmail);
        verify(passwordEncoder).matches(mockPassword, mockUser.getPassword());
        verify(passwordEncoder).encode(mockNewPassword);
        verify(userRepository).save(mockUser);
        verify(jwtService).generateToken(mockUser);
        verify(emailService).sendEmail(mockEmail, mockSubject, mockBody);
    }

    @Test
    public void testChangePasswordUserNotFound() {
        when(jwtService.extractUserName(mockToken)).thenReturn(mockEmail);
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.changePassword(mockToken, mockPassword, mockNewPassword));
    }

    @Test
    public void testChangePasswordInvalidCredentials() {
        when(jwtService.extractUserName(mockToken)).thenReturn(mockEmail);
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(mockPassword, mockUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.changePassword(mockToken, mockPassword, mockNewPassword));
    }

    @Test
    public void testGetRolesByToken() {
        when(jwtService.extractUserName(mockToken)).thenReturn(mockEmail);
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));

        List<String> roles = List.of(authService.getRolesByToken(mockToken));

        assert roles.size() == 2;
        assert roles.contains(Role.ADMIN.name());
        assert roles.contains(Role.COORDINADOR.name());
        verify(jwtService).extractUserName(mockToken);
        verify(userRepository).findByEmail(mockEmail);
    }

    @Test
    public void testGetEmailByToken() {
        when(jwtService.extractUserName(mockToken)).thenReturn(mockEmail);
        
        String email = authService.getEmailByToken(mockToken);

        assert email.equals(mockEmail);
        verify(jwtService).extractUserName(mockToken);
    }

    @Test
    public void testGetNameByToken() {
        when(jwtService.extractUserName(mockToken)).thenReturn(mockEmail);
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));

        String name = authService.getNameByToken(mockToken);

        assert name.equals(mockUser.getName() + " " + mockUser.getLastName());
        verify(jwtService).extractUserName(mockToken);
        verify(userRepository).findByEmail(mockEmail);
    }

}
