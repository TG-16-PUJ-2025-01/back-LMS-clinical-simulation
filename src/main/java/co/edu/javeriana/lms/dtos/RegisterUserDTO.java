package co.edu.javeriana.lms.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    private String email;
    private String password;
    private List<String> roles;
    private String name;
    private String lastName;
    private int instituionalId;
}
