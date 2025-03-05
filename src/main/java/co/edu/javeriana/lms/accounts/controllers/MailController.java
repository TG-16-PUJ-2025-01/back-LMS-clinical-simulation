package co.edu.javeriana.lms.accounts.controllers;

import co.edu.javeriana.lms.accounts.dtos.EmailConfigDto;
import co.edu.javeriana.lms.accounts.services.EmailService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/config")
    public ResponseEntity<?> updateMailConfig(@Valid @RequestBody EmailConfigDto emailConfigDto) {
        String host = emailConfigDto.getHost();
        String username = emailConfigDto.getUsername();
        String password = emailConfigDto.getPassword();
        emailService.updateMailConfig(host, username, password);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Email configuration updated successfully", null, null));
    }
}