package co.edu.javeriana.lms.dtos;

import java.util.Set;

import co.edu.javeriana.lms.models.Role;
import co.edu.javeriana.lms.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ResponseUserDTO {
    private String email;
    private String name;
    private String lastName;
    private int instituionalId;
    private Set<Role> roles;

    public void userToResponseUserDTO(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.instituionalId = user.getInstituionalId();
        this.roles = user.getRoles();
    }
}
