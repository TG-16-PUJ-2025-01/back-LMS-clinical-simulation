package co.edu.javeriana.lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import co.edu.javeriana.lms.dtos.LoginDTO;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.repositories.UserRepository;
import co.edu.javeriana.lms.services.JwtService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        
        // Verificar si el usuario existe
        User user = userRepository.findByEmail(email)
                .orElse(null); // Devuelve null si no se encuentra

        // Si el usuario no existe o la contraseña no coincide
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            // Retornar Bad Request con mensaje adecuado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // Generación de JWT
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(token);
    }
}
