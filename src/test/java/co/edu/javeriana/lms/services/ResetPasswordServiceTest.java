package co.edu.javeriana.lms.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

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

    @Test
    public void testCreatePasswordResetToken() {
    }

    @Test
    public void testSentPasswordResetEmail() {
    }

    @Test
    public void testVerifyResetToken() {
    }

    @Test
    public void testResetPassword() {
    }

}
