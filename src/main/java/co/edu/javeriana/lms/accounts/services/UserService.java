package co.edu.javeriana.lms.accounts.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.config.security.PasswordGenerator;
import jakarta.persistence.EntityNotFoundException;
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

    public Page<User> getAllUsers(String filter, Integer page, Integer size, String sort, Boolean asc) {
        log.info("Getting all users");
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        if (filter == "email") {
            sortOrder = asc ? Sort.by("email").ascending() : Sort.by("email").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return userRepository.findAllFiltered(filter, pageable);
    }

    public User addUser(User user) {
        log.info("Creating user: " + user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        String password = PasswordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        String subject = "Bienvenido a LMS - Tus credenciales de acceso";
        String body = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2>Bienvenido a LMS</h2>
            <p>Hola <strong>%s</strong>,</p>
            <p>Tu cuenta ha sido creada exitosamente.</p>
            <p><strong>Tu contraseña temporal es:</strong> <span style="color: #2c3e50;">%s</span></p>
            <p>Por favor, cambia tu contraseña después de iniciar sesión.</p>
            <hr>
            <p style="font-size: small; color: #999;">Este es un mensaje automático, por favor no responder.</p>
        </body>
        </html>
        """.formatted(user.getEmail(), password);
        new Thread(() -> emailService.sendEmail(user.getEmail(), subject, body)).start();

        return savedUser;
    }

    public User addUserExcel(User user) {
        log.info("Creating user: " + user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            return userRepository.findByEmail(user.getEmail()).orElseThrow();
        }
        String password = PasswordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        User savedUser =  userRepository.save(user);

        String subject = "Bienvenido a LMS - Tus credenciales de acceso";
        String body = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2>Bienvenido a LMS</h2>
            <p>Hola <strong>%s</strong>,</p>
            <p>Tu cuenta ha sido creada exitosamente.</p>
            <p><strong>Tu contraseña temporal es:</strong> <span style="color: #2c3e50;">%s</span></p>
            <p>Por favor, cambia tu contraseña después de iniciar sesión.</p>
            <hr>
            <p style="font-size: small; color: #999;">Este es un mensaje automático, por favor no responder.</p>
        </body>
        </html>
        """.formatted(user.getEmail(), password);

        new Thread(() -> emailService.sendEmail(user.getEmail(), subject, body)).start();

        return savedUser;
    }

    public User findById(Long id) {
        log.info("Getting user by id: " + id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public User updateUserById(Long id, User user) {
        log.info("Updating user by id: " + id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setInstitutionalId(user.getInstitutionalId());
        existingUser.setRoles(user.getRoles());
        return userRepository.save(existingUser);
    }

    public User setPreferredRoleToUser(String email, Role role) {
        log.info("Setting default role to user by name: " + email);
        User existingUser = userRepository.findByEmail(email).orElseThrow();
        existingUser.setPreferredRole(role);
        return userRepository.save(existingUser);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by username (email): " + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting user by id: " + id);
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
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