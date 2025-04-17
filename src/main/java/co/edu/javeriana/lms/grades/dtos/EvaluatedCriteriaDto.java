package co.edu.javeriana.lms.grades.dtos;

import java.util.UUID;

import co.edu.javeriana.lms.grades.models.EvaluatedCriteria;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluatedCriteriaDto {
    private String comment;

    @Min(value = 0, message = "Criteria id must be greater than or equal to 0")
    @Max(value = 5, message = "Criteria id must be less than or equal to 5")
    private Float score;

    public EvaluatedCriteria toEvaluatedCriteria() {
        UUID uuid = UUID.randomUUID();
        return new EvaluatedCriteria(uuid, this.comment, this.score);
    }
}
