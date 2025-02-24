package co.edu.javeriana.lms.dtos;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiResponseDtos<T> {
    private Integer status;
    private String message;
    private T data;
    @Nullable
    private Object metadata;
}