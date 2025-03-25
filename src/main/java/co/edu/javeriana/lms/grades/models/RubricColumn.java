package co.edu.javeriana.lms.grades.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RubricColumn {
    private String title;
    private ScoringPair scoringScale;
}
