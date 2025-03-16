package co.edu.javeriana.lms.accounts.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenWithEmailDto {
    @NotBlank(message = "Token is required")
    private String token;
    @NotBlank(message = "Email is required")
    private String email;

}
