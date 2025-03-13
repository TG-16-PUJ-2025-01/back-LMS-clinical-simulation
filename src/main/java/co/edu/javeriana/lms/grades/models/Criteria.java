package co.edu.javeriana.lms.grades.models;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {
    
    private UUID id;
    private String name;
    private Integer points;
    private List<ScoringPair> scoringScale;  // 👈 Aquí se reemplaza Pair<Long, Long>
    private List<String> scoringScaleDescription;
    private Integer score;
}
