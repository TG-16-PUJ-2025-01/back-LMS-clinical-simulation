package co.edu.javeriana.lms.subjects.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    @NotNull(message = "Id Javeriana is required")
    @Positive(message = "Id Javeriana must be a positive number")
    private Long javerianaId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Coordinator ID is required")
    @Positive(message = "Coordinator ID must be a positive number")
    private Long coordinatorId;
}
