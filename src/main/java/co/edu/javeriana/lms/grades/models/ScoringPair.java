package co.edu.javeriana.lms.grades.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringPair {
    private Long lowerValue;
    private Long upperValue;
}
