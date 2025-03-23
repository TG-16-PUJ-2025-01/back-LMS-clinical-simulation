package co.edu.javeriana.lms.practices.dtos;

import java.util.Date;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimulationByTimeSlotDto {
    @NotNull(message = "Practice ID is required")
    private Long practiceId; 
    
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    @NotNull(message = "Start date and time is required")
    @Future(message = "Start date and time must be in the future")
    private Date startDateTime;
    
    @Future(message = "Start date and time must be in the future")
    @NotNull(message = "End date and time is required")
    private Date endDateTime;
}
