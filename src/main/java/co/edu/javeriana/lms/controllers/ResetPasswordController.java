package co.edu.javeriana.lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.accounts.models.PasswordResetToken;
import co.edu.javeriana.lms.dtos.PasswordResetDto;
import co.edu.javeriana.lms.dtos.UsernameDto;
import co.edu.javeriana.lms.services.ResetPasswordService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/reset-password")
public class ResetPasswordController {

    @Autowired
    private ResetPasswordService resetPasswordService;

    @PostMapping("/request")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody UsernameDto usernameDTO) {
        String email = usernameDTO.getEmail();
        PasswordResetToken passwordResetToken = resetPasswordService.createPasswordResetToken(email);
        resetPasswordService.sentPasswordResetEmail(email, passwordResetToken.getToken());
        return ResponseEntity.ok("Password reset requested");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPasswordReset(@Valid @RequestBody PasswordResetDto passwordResetDTO) {
        String email = passwordResetDTO.getEmail();
        String token = passwordResetDTO.getToken();
        if(resetPasswordService.verifyResetToken(email, token)) {
            return ResponseEntity.ok("Password reset verified");   
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDto passwordResetDTO) {
        
        String email = passwordResetDTO.getEmail();
        String token = passwordResetDTO.getToken();
        String password = passwordResetDTO.getPassword();

        resetPasswordService.resetPassword(email, token, password);
        return ResponseEntity.ok("Password reseted successfully");
    }
}
