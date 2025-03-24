package co.edu.javeriana.lms.accounts.dtos;

import java.util.Set;

import co.edu.javeriana.lms.accounts.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private Set<Role> roles;
}
