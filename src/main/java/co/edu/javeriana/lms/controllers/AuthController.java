package co.edu.javeriana.lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.javeriana.lms.dtos.LoginDTO;
import co.edu.javeriana.lms.dtos.PasswordResetDTO;
import co.edu.javeriana.lms.models.PasswordResetToken;
import co.edu.javeriana.lms.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        
        String token = userService.login(email, password);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        
        String token = userService.changePassword(email, password);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody String email) {
        PasswordResetToken passwordResetToken = userService.createPasswordResetToken(email);
        userService.sentPasswordResetEmail(email, passwordResetToken.getToken());
        return ResponseEntity.ok("Password reset requested");
    }

    @PostMapping("/verify-password-reset")
    public ResponseEntity<?> verifyPasswordReset(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        String email = passwordResetDTO.getEmail();
        String token = passwordResetDTO.getToken();
        if(userService.verifyResetToken(email, token)) {
            return ResponseEntity.ok("Password reset verified");   
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        
        String email = passwordResetDTO.getEmail();
        String token = passwordResetDTO.getToken();
        String password = passwordResetDTO.getPassword();

        userService.resetPassword(email, token, password);
        return ResponseEntity.ok("Password reseted successfully");
    }
}
