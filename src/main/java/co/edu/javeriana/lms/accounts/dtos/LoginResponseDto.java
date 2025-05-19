package co.edu.javeriana.lms.accounts.dtos;

import java.util.Set;

import co.edu.javeriana.lms.accounts.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private Set<Role> roles;
    private Role preferredRole;
}
