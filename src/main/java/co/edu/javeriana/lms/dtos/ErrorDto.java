package co.edu.javeriana.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDto {
    private String message;
    private Object data = null;

    public ErrorDto(String message) {
        this.message = message;
    }
}