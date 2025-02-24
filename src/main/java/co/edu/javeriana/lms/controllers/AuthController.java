package co.edu.javeriana.lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.javeriana.lms.dtos.ChangePasswordDto;
import co.edu.javeriana.lms.dtos.LoginDto;
import co.edu.javeriana.lms.services.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        
        String token = authService.login(email, password);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        String password = changePasswordDto.getPassword();
        String newPassword = changePasswordDto.getNewPassword();
        log.info("Token: " + token);
        token = token.substring(7);
        
        String newToken = authService.changePassword(token, password, newPassword);
        return ResponseEntity.ok(newToken);
    }
}