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

    public String changePassword(String email, String password) {
        log.info("Changing password for user: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        String token = jwtService.generateToken(user);

        String subject = "Cambio de contraseña LMS";
        String body = "Hola " + user.getEmail() + ",\n\nTu contraseña ha sido cambiada con éxito.\n" +
                  "Si no fuiste tú, por favor, contacta al administrador.";
        emailService.sendEmail(user.getEmail(), subject, body);

        return token;
    }
    
}
