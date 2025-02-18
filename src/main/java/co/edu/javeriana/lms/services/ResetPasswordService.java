package co.edu.javeriana.lms.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.PasswordResetToken;
import co.edu.javeriana.lms.repositories.PasswordResetTokenRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResetPasswordService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private AuthService authService;

    public PasswordResetToken createPasswordResetToken (String email) {
        log.info("Creating password reset token for user: " + email);
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("User not found");
        }
        
        String token = UUID.randomUUID().toString().substring(0, 6);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUserEmail(email);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public void sentPasswordResetEmail(String email, String token) {
        log.info("Sending password reset email to: " + email);
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("User not found");
        }
        String subject = "Restablecer contraseña LMS";
        String body = "Hola " + email + ",\n\nPara restablecer tu contraseña, utiliza el siguiente código: " + token + "\n\n" +
                  "Si no solicitaste el cambio de contraseña, por favor, ignora este mensaje.";
        emailService.sendEmail(email, subject, body);
    }

    public Boolean verifyResetToken (String email, String token) {
        log.info("Verifying password reset token for user: " + email);
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if (!passwordResetToken.getUserEmail().equals(email)) {
            throw new RuntimeException("Invalid token");
        }
        if (passwordResetToken.isExpired()) {
            throw new RuntimeException("Token expired");
        }
        return true;
    }

    public void resetPassword(String email, String token, String password) {
        log.info("Resetting password for user: " + email);
        if (!verifyResetToken(email, token)) {
            throw new RuntimeException("Invalid token");
        }
        authService.changePassword(email, password);
    }

    @Scheduled(fixedRate = 1)
    public void deleteExpiredTokens() {
        log.info("Deleting expired password reset tokens");
        passwordResetTokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
    }
}
