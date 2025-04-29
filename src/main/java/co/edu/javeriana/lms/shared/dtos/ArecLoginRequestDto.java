package co.edu.javeriana.lms.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ArecLoginRequestDto {
    private String user;
    private String authorization;
    private String site;
    private String tabId;
}
