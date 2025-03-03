package co.edu.javeriana.lms.accounts.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailConfigDto {
    @NotBlank(message = "Host is required")
    private String host;
    @NotBlank(message = "Username is required")
    @Email(message = "Username should be a valid email")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
}
