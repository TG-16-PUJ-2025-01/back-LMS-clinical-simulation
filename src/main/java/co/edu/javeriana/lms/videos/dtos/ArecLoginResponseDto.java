package co.edu.javeriana.lms.videos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ArecLoginResponseDto {
    private String session;
}
