package co.edu.javeriana.lms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.User;
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

    public List<User> findAllCoordinators() {
        log.info("Getting all coordinators");
        return userRepository.findAllCoordinators();
    }

    public List<User> findAllProfessors() {
        log.info("Getting all professors");

        return userRepository.findAllProfessors();

    }

}

