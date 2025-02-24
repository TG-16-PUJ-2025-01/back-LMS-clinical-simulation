package co.edu.javeriana.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDtos {
    private String message;
    private Object data = null;

    public ErrorDtos(String message) {
        this.message = message;
    }
}