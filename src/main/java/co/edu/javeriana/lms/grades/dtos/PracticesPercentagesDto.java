package co.edu.javeriana.lms.grades.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticesPercentagesDto {
    private List<PracticePercentageDto> practicesPercentages;
}
