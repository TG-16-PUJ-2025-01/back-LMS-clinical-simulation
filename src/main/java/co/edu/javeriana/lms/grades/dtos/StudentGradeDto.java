package co.edu.javeriana.lms.grades.dtos;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeDto {
    private String studentName;
    private Map<String, Float> practiceGrades; // Nombre de práctica -> nota
    private Float finalGrade;

    public void addPracticeGrade(String practiceName, Float grade) {

        practiceGrades.put(practiceName, grade);
    }

    public StudentGradeDto(String studentName) {
        this.studentName = studentName;
        this.practiceGrades = new HashMap<>();
        this.finalGrade = 0f;
    }
}
