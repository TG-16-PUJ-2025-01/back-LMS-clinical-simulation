package co.edu.javeriana.lms.dtos;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditVideoDTO {

    @NotBlank(message = "The name is required")
    private String name;

    @NotNull(message = "The expiration date is required")
    private Date expirationDate;

}
