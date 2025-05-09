package co.edu.javeriana.lms.grades.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EvaluatedCriteria {
    private UUID id;
    private String comment;
    private Float score;
}
