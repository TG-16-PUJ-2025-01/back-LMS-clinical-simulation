package co.edu.javeriana.lms.practices.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlotDto {
    private String startDateTime;
    private String endDateTime;
}
