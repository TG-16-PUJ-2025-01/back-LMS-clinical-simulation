package co.edu.javeriana.lms.videos.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditVideoDto {

    @NotBlank(message = "The name is required")
    private String name;

}
