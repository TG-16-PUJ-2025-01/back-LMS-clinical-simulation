package co.edu.javeriana.lms.grades.dtos;

import java.util.List;

import co.edu.javeriana.lms.grades.models.Rubric;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RubricDto {
    @Valid
    private List<EvaluatedCriteriaDto> evaluatedCriterias;

    @Valid
    private EvaluatedCriteriaDto total;

    public Rubric toRubric() {
        Rubric rubric = new Rubric();
        rubric.setEvaluatedCriterias(this.evaluatedCriterias.stream().map(EvaluatedCriteriaDto::toEvaluatedCriteria).toList());
        rubric.setTotal(this.total.toEvaluatedCriteria());
        return rubric;
    }
}
