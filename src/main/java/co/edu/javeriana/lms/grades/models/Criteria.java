package co.edu.javeriana.lms.grades.models;

import java.util.List;

import ch.qos.logback.core.joran.sanity.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {
    
    private String name;
    private String description;
    private Integer points;
    private List<Pair<Long, Long>> scoringScale;
    private List<String> scoringScaleDescription;
    private Long score;

}
