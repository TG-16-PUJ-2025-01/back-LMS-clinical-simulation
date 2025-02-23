package co.edu.javeriana.lms.dtos;
import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDTO {

    @NotNull(message = "Id Javeriana is required")
    @Positive(message = "Id Javeriana must be a positive number")
    private Long javerianaId;
     
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Id Professor is required")
    @Positive(message = "Id Professor must be a positive number")
    private Long professorId;

    @NotNull(message = "Id course is required")
    @Positive(message = "Id course must be a positive number")
    private Long courseId;


    @NotNull(message = "Beginning date is required")
    private Date beginningDate;
}
