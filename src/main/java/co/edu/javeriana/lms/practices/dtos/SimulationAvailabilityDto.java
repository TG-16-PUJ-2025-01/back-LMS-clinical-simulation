package co.edu.javeriana.lms.practices.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationAvailabilityDto {
    private Long simulationId;
    private Integer groupNumber;
    private Date startDateTime;
    private Date endDateTime;
    private Boolean available;
}
