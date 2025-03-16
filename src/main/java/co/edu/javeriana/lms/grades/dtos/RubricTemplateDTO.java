package co.edu.javeriana.lms.grades.dtos;

import java.sql.Date;
import java.util.List;

import co.edu.javeriana.lms.grades.models.Criteria;
import io.micrometer.common.lang.Nullable;
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
    @Nullable
    private List<Long> courses;
    @NotNull(message = "creation date is required")
    private Date creationDate;
    @Nullable
    private Long practiceId;
    @NotNull(message = "archived is required")
    Boolean archived;
}
