package co.edu.javeriana.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PasswordResetDTO {
    String email;
    String password;
    String token;
}
