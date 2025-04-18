package co.edu.javeriana.lms.grades.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticePercentageDto {
    Long practiceId;
    Float percentage;
}
