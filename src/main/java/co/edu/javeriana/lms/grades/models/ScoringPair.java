package co.edu.javeriana.lms.grades.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoringPair {
    private Long lowerValue;
    private Long upperValue;
}
