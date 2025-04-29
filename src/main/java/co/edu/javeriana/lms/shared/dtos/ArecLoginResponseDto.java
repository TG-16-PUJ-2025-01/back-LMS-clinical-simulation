package co.edu.javeriana.lms.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ArecLoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
