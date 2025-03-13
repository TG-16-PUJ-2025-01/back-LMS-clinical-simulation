package co.edu.javeriana.lms.grades.models;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Rubric")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rubricId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") 
    private List<EvaluatedCriteria> evaluatedCriterias;

    @ManyToOne
    @JoinColumn(nullable = true)
    private RubricTemplate rubricTemplate;
    
    @OneToOne
    @JoinColumn(nullable = false)
    private Simulation simulation;

    public void changeEvaluatedCriteria(List<Criteria> previousCriterias) {
        
        //Comparar entre previousCriteria y evaluatedCriterias
        //Si el id es igual se sabe que se debe dejar, de lo contrario
        //se debe eliminar el evaluatedCriteria

        for (EvaluatedCriteria evaluatedCriteria : evaluatedCriterias) {
            
            boolean found = false;

            for (Criteria previousCriteria : previousCriterias) {
                if(evaluatedCriteria.getId().equals(previousCriteria.getId())){
                    found = true;
                }
            }

            if(!found){
                this.evaluatedCriterias.remove(evaluatedCriteria);
            }

        }

    }

    public void addEvaluatedCriteria(EvaluatedCriteria evaluatedCriteria) {
        this.evaluatedCriterias.add(evaluatedCriteria);
    }
}
