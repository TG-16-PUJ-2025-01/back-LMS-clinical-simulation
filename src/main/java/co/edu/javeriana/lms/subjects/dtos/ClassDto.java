package co.edu.javeriana.lms.subjects.dtos;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDto {

    @NotNull(message = "Id Javeriana is required")
    @Positive(message = "Id Javeriana must be a positive number")
    private Long javerianaId;

    @NotNull(message = "Id Professor is required")
    private List<Long> professorsIds;

    @NotNull(message = "Id course is required")
    @Positive(message = "Id course must be a positive number")
    private Long courseId;


    @NotNull(message = "Period is required")
    private String period;

    private Integer numberOfParticipants;
}
