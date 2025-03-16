package co.edu.javeriana.lms.grades.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum GradeStatus {
    PENDING,
    REGISTERED,
    NOT_EVALUABLE
}
