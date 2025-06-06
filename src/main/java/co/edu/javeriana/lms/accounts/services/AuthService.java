package co.edu.javeriana.lms.accounts.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.dtos.LoginResponseDto;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.shared.services.JwtService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    public LoginResponseDto login(String email, String password) {
        log.info("Logging in user: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponseDto(token, user.getRoles(), user.getPreferredRole());
    }

    public String changePassword(String token, String password, String newPassword) {
        log.info("Changing password for token: " + token);
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        String subject = "Cambio de contraseña LMS";
        String body = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2 style="color: #2c3e50;">Cambio de contraseña exitoso</h2>
            <p>Hola <strong>%s</strong>,</p>
            <p>Tu contraseña ha sido <strong>cambiada exitosamente</strong>.</p>
            <p>Si no realizaste este cambio, por favor <span style="color: red;">contacta al administrador de inmediato</span>.</p>
            <br>
            <p style="font-size: small; color: #999;">Este es un mensaje automático del sistema LMS. Por favor, no respondas a este correo.</p>
        </body>
        </html>
        """.formatted(user.getEmail());
        new Thread(() -> emailService.sendEmail(user.getEmail(), subject, body)).start();
        String newToken = jwtService.generateToken(user);
        return newToken;
    }

    public String[] getRolesByToken(String token) {
        log.info("Getting roles for token: " + token);
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles().stream().map(role -> role.name()).toArray(String[]::new);
    }

    public String getEmailByToken(String token) {
        log.info("Getting email for token: " + token);
        return jwtService.extractUserName(token);
    }

    public String getNameByToken(String token) {
        log.info("Getting name for token: " + token);
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        String firstName = user.getName();
        String lastName = user.getLastName();
        return firstName + " " + lastName;
    }

    public Long getUserIdByToken(String token) {
        log.info("Getting user ID for token: " + token);
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getId();
    }
}