package co.edu.javeriana.lms.accounts.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            throw new UsernameNotFoundException("User not found with email: " + email);
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
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        String subject = "Restablecer contraseña LMS";
        String body = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2 style="color: #2c3e50;">Solicitud para restablecer tu contraseña</h2>
            <p>Hola <strong>%s</strong>,</p>
            <p>Hemos recibido una solicitud para restablecer tu contraseña en el sistema LMS.</p>
            <p>Utiliza el siguiente código para continuar con el proceso:</p>
            <div style="background-color: #f2f2f2; padding: 10px 20px; display: inline-block; font-size: 18px; font-weight: bold; border-radius: 6px; border: 1px solid #ccc;">
            %s
            </div>
            <p style="margin-top: 20px;">Si no solicitaste este cambio, puedes ignorar este mensaje con seguridad.</p>
            <hr>
            <p style="font-size: small; color: #999;">Este es un mensaje automático del sistema LMS. Por favor, no respondas a este correo.</p>
        </body>
        </html>
        """.formatted(email, token);

        new Thread(() -> emailService.sendEmail(email, subject, body)).start();
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        passwordResetTokenRepository.deleteByToken(token);
        String subject = "Cambio de contraseña LMS";
        String body = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2 style="color: #2c3e50;">Contraseña actualizada exitosamente</h2>
            <p>Hola <strong>%s</strong>,</p>
            <p>Te informamos que tu contraseña ha sido <strong>cambiada con éxito</strong>.</p>
            <p>Si tú <u>no realizaste</u> este cambio, por favor contacta inmediatamente al administrador del sistema.</p>
            <br>
            <p style="font-size: small; color: #999;">Este es un mensaje automático generado por el sistema LMS. No respondas a este correo.</p>
        </body>
        </html>
        """.formatted(user.getEmail());

        new Thread(() -> emailService.sendEmail(user.getEmail(), subject, body)).start();
    }
}
