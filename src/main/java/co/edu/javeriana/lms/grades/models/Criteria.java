package co.edu.javeriana.lms.grades.models;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Criteria {
    private UUID id;
    private String name;
    private Double weight;
    private List<String> scoringScaleDescription;
}
