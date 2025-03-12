package co.edu.javeriana.lms.grades.models;

import java.util.List;
import java.util.UUID;

import ch.qos.logback.core.joran.sanity.Pair;
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
    private List<Pair<Long, Long>> scoringScale;
    private List<String> scoringScaleDescription;//tambien puede ser la descripcion de la nota dada
    private Integer score;

}
