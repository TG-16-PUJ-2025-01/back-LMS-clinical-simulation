package co.edu.javeriana.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDTO {
    private String message;
    private Object data = null;

    public ErrorDTO(String message) {
        this.message = message;
    }
}