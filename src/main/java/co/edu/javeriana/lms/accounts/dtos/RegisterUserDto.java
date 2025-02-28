package co.edu.javeriana.lms.accounts.dtos;

import java.util.Set;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private int institutionalId;

    @NotNull(message = "Roles are required")
    @Size(min = 1, message = "At least one role is required")
    private Set<String> roles;

    public User toUser() {
        return User.builder()
                .email(this.email)
                .password(null)
                .name(this.name)
                .lastName(this.lastName)
                .institutionalId(this.institutionalId)
                .roles(this.roles.stream().map(Role::valueOf).collect(java.util.stream.Collectors.toSet()))
                .build();
    }
}
