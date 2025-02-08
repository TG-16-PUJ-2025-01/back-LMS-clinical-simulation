package co.edu.javeriana.lms.dtos;

import java.util.Set;

import co.edu.javeriana.lms.models.Role;
import co.edu.javeriana.lms.models.User;
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
public class RegisterUserDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must end in javeriana.edu.co", regexp = ".*javeriana.edu.co$")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private int instituionalId;

    @NotNull(message = "Roles are required")
    @Size(min = 1, message = "At least one role is required")
    private Set<Role> roles;

    public User toUser() {
        return User.builder()
                .email(this.email)
                .password(null)
                .name(this.name)
                .lastName(this.lastName)
                .instituionalId(this.instituionalId)
                .roles(this.roles)
                .build();
    }
}
