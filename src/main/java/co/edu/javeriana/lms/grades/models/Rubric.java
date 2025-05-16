package co.edu.javeriana.lms.grades.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.practices.models.Simulation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Rubric")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rubricId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<EvaluatedCriteria> evaluatedCriterias;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private EvaluatedCriteria total;

    @ManyToOne
    @JoinColumn(nullable = true)
    private RubricTemplate rubricTemplate;

    @OneToOne
    @JoinColumn(nullable = true, name = "simulationId")
    @JsonIgnore
    private Simulation simulation;

    public void changeEvaluatedCriteria(List<Criteria> newCriteria) {
        List<EvaluatedCriteria> updatedEvaluatedCriterias = new ArrayList<>();

        for (Criteria criteria : newCriteria) {
            this.evaluatedCriterias.stream()
                    .filter(ec -> ec.getId().equals(criteria.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            ec -> updatedEvaluatedCriterias.add(ec),
                            () -> updatedEvaluatedCriterias.add(new EvaluatedCriteria(criteria.getId(), "", 0F)));
        }

        // Reemplazar la lista completa
        this.evaluatedCriterias = updatedEvaluatedCriterias;
    }

    public void addEvaluatedCriteria(EvaluatedCriteria evaluatedCriteria) {
        this.evaluatedCriterias.add(evaluatedCriteria);
    }
}
