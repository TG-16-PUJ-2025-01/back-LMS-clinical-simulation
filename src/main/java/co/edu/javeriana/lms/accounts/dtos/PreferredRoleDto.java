package co.edu.javeriana.lms.accounts.dtos;

import co.edu.javeriana.lms.accounts.models.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferredRoleDto {
    @NotNull(message = "Role is required")
    private Role role;
}
