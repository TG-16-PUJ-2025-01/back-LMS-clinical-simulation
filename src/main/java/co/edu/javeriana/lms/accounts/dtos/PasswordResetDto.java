package co.edu.javeriana.lms.accounts.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PasswordResetDto {
    String email;
    String password;
    String token;
}
