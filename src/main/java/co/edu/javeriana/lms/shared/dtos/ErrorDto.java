package co.edu.javeriana.lms.shared.dtos;

import co.edu.javeriana.lms.shared.errors.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDto {
    private String message;
    private Object data = null;
    private ErrorCode code = null;

    public ErrorDto(String message) {
        this.message = message;
    }

    public ErrorDto(String message, ErrorCode code) {
        this.message = message;
        this.code = code;
    }

}