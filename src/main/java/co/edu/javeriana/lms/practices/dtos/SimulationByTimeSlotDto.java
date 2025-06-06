package co.edu.javeriana.lms.practices.dtos;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimulationByTimeSlotDto {
    @NotNull(message = "Practice ID is required")
    private Long practiceId; 
    
    @NotNull(message = "Room ID is required")
    private List<Long> roomIds;
    
    @NotNull(message = "Start date and time is required")
    private Date startDateTime;
    
    @NotNull(message = "End date and time is required")
    private Date endDateTime;
}
