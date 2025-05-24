package co.edu.javeriana.lms.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.accounts.models.PasswordResetToken;
import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.PasswordResetTokenRepository;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.accounts.services.EmailService;
import co.edu.javeriana.lms.accounts.services.ResetPasswordService;

@SpringBootTest
@ActiveProfiles("test")
public class ResetPasswordServiceTest {

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static String mockEmail = "mock@email.com";
    private static PasswordResetToken mockToken;
    private static String mockTokenValue = "mockToken";
    private static String mockEncodedPassword = "encodedMockPassword";
    private static String mockNewPassword = "newMockPassword";
    private static User mockUser;

    @BeforeAll
    public static void setUpAll() {
        mockToken = PasswordResetToken.builder()
                .id(1L)
                .token(mockTokenValue)
                .userEmail(mockEmail)
                .expirationDate(LocalDateTime.now().plusMinutes(30))
                .build();

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
    public void testCreatePasswordResetToken() {
        when(userRepository.existsByEmail(mockEmail)).thenReturn(true);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(mockToken);

        PasswordResetToken createdToken = resetPasswordService.createPasswordResetToken(mockEmail);

        assert createdToken != null;
        assert createdToken.getToken().equals(mockTokenValue);
        assert createdToken.getUserEmail().equals(mockEmail);
        assert createdToken.getExpirationDate().isAfter(LocalDateTime.now().plusMinutes(29));
        assert createdToken.getExpirationDate().isBefore(LocalDateTime.now().plusMinutes(31));
    }

    @Test
    public void testSentPasswordResetEmail() {
        when(userRepository.existsByEmail(mockEmail)).thenReturn(true);
        when(passwordResetTokenRepository.findByToken(mockTokenValue)).thenReturn(Optional.of(mockToken));

        resetPasswordService.sentPasswordResetEmail(mockEmail, mockTokenValue);
    }

    @Test
    public void testVerifyResetToken() {
        when(passwordResetTokenRepository.findByToken(mockTokenValue)).thenReturn(Optional.of(mockToken));

        Boolean isValid = resetPasswordService.verifyResetToken(mockEmail, mockTokenValue);

        assert isValid;
        assert mockToken.getUserEmail().equals(mockEmail);
        assert !mockToken.isExpired();
    }

    @Test
    public void testResetPassword() {
        when(passwordResetTokenRepository.findByToken(mockTokenValue)).thenReturn(Optional.of(mockToken));
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(any(String.class))).thenReturn(mockEncodedPassword);

        resetPasswordService.resetPassword(mockEmail, mockTokenValue, mockNewPassword);

        verify(userRepository).save(any());
        verify(passwordEncoder).encode(mockNewPassword);
    }

}
