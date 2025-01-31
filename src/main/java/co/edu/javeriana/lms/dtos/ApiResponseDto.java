package co.edu.javeriana.lms.dtos;

import lombok.Data;

@Data
public class ApiResponseDto<T> {
    private String status;
    private String message;
    private T data;
    private Object metadata;
}
