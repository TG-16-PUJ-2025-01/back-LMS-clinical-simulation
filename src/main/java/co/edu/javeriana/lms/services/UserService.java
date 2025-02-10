package co.edu.javeriana.lms.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.PasswordResetToken;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.repositories.PasswordResetTokenRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import co.edu.javeriana.lms.utils.PasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

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
        changePassword(email, password);
    }


    public User createUser(User user) {
        log.info("Creating user: " + user.getEmail());
        String password = PasswordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        User savedUser =  userRepository.save(user);

        String subject = "Bienvenido a LMS - Tus credenciales de acceso";
        String body = "Hola " + user.getEmail() + ",\n\nTu cuenta ha sido creada exitosamente.\n" +
                  "Tu contraseña temporal es: " + password + "\n\nPor favor, cambia tu contraseña después de iniciar sesión.";
        emailService.sendEmail(user.getEmail(), subject, body);

        return savedUser;
    }

    public User getUserByEmail(String email) {
        log.info("Getting user by email: " + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by username (email): " + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public void deleteByEmail(String email) {
        log.info("Deleting user by email: " + email);
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteByEmail(email);
    }
}

