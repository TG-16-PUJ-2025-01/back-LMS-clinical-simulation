package co.edu.javeriana.lms.subjects.dtos;

import java.util.List;

import co.edu.javeriana.lms.subjects.models.ClassModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto {

    private Long courseId;

    @NotNull(message = "Id Javeriana is required")
    @Positive(message = "Id Javeriana must be a positive number")
    private Long javerianaId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Coordinator ID is required")
    @Positive(message = "Coordinator ID must be a positive number")
    private Long coordinatorId;

    private List<ClassModel> classes;

    @NotNull(message = "faculty is required")
    private String faculty;

    @NotNull(message = "department is required")
    private String department;

    @NotNull(message = "program is required")
    private String program;

    @NotNull(message = "semester is required")
    @Positive(message = "semester must be a positive number")
    private Integer semester;
}
