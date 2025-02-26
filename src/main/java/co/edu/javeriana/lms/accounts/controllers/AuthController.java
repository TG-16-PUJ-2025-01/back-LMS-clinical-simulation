package co.edu.javeriana.lms.accounts.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.javeriana.lms.accounts.dtos.ChangePasswordDto;
import co.edu.javeriana.lms.accounts.dtos.LoginDto;
import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
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
        token = token.substring(7);
        
        String newToken = authService.changePassword(token, password, newPassword);
        return ResponseEntity.ok(newToken);
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRolesByToken(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Roles retrieved successfully", authService.getRolesByToken(token), null));
    }
}