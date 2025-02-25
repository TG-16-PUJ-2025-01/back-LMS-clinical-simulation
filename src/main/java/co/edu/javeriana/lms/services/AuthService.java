package co.edu.javeriana.lms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.repositories.UserRepository;
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

    public String login(String email, String password) {
        log.info("Logging in user: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")); 

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return token;
    }

    public String changePassword(String token, String password, String newPassword) {
        log.info("Changing password for token: " + token);
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        String subject = "Cambio de contraseña LMS";
        String body = "Hola " + user.getEmail() + ",\n\nTu contraseña ha sido cambiada con éxito.\n" +
                    "Si no fuiste tú, por favor, contacta al administrador.";
        emailService.sendEmail(user.getEmail(), subject, body);
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
}
