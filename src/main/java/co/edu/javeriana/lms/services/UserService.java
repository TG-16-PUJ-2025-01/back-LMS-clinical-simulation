package co.edu.javeriana.lms.services;

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
        User savedUser =  userRepository.save(user);

        String subject = "Bienvenido a LMS - Tus credenciales de acceso";
        String body = "Hola " + user.getEmail() + ",\n\nTu cuenta ha sido creada exitosamente.\n" +
                  "Tu contraseña temporal es: " + password + "\n\nPor favor, cambia tu contraseña después de iniciar sesión.";
        emailService.sendEmail(user.getEmail(), subject, body);

        return savedUser;
    }

    public User getUserById(Long id) {
        log.info("Getting user by id: " + id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserById(Long id, User user) {
        log.info("Updating user by id: " + id);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        User existingUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setInstitutionalId(user.getInstitutionalId());
        existingUser.setRoles(user.getRoles());
        return userRepository.save(user);
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
            throw new RuntimeException("User not found");
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

