package co.edu.javeriana.lms.grades.dtos;

import java.util.List;

import co.edu.javeriana.lms.grades.models.Criteria;
import co.edu.javeriana.lms.grades.models.RubricColumn;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RubricTemplateDTO {

    @NotNull(message = "title is required")
    String title;
    @NotNull(message = "criteria is required")
    private List<Criteria> criteria;
    @NotNull(message = "columns is required")
    private List<RubricColumn> columns;
    private List<Long> courses;
    private Long practiceId;
    @NotNull(message = "archived is required")
    Boolean archived;
}
