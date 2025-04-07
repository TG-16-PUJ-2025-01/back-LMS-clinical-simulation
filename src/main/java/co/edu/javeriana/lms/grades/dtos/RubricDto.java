package co.edu.javeriana.lms.grades.dtos;

import java.util.List;

import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.practices.models.Simulation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RubricDto {
    @Valid
    private List<EvaluatedCriteriaDto> evaluatedCriterias;

    @NotNull(message = "rubricTemplateId is required")
    private Long rubricTemplateId;

    @NotNull(message = "simulationId is required")
    private Long simulationId;

    public Rubric toRubric() {
        Rubric rubric = new Rubric();
        rubric.setEvaluatedCriterias(this.evaluatedCriterias.stream().map(EvaluatedCriteriaDto::toEvaluatedCriteria).toList());
        rubric.setSimulation(Simulation.builder().simulationId(this.simulationId).build());
        rubric.setRubricTemplate(RubricTemplate.builder().rubricTemplateId(this.rubricTemplateId).build());
        return rubric;
    }
}
