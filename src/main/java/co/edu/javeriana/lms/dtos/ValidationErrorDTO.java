package co.edu.javeriana.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidationErrorDTO {
    private String field;
    private String message;
}