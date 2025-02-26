package co.edu.javeriana.lms.accounts.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.PasswordResetToken;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.PasswordResetTokenRepository;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import jakarta.transaction.Transactional;
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
    private PasswordEncoder passwordEncoder;

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

    @Transactional
    public void resetPassword(String email, String token, String password) {
        log.info("Resetting password for user: " + email);
        if (!verifyResetToken(email, token)) {
            throw new RuntimeException("Invalid token");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        passwordResetTokenRepository.deleteByToken(token);
        String subject = "Cambio de contraseña LMS";
        String body = "Hola " + user.getEmail() + ",\n\nTu contraseña ha sido cambiada con éxito.\n" +
                    "Si no fuiste tú, por favor, contacta al administrador.";
        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
