package co.edu.javeriana.lms.practices.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimulationDto {
    @NotBlank(message = "Practice ID is required")
    private Long practiceId; 
    
    @NotBlank(message = "Room ID is required")
    private Long roomId;
    
    @NotBlank(message = "Start date and time is required")
    @Future(message = "Start date and time must be in the future")
    private LocalDateTime startDateTime;
    
    @Future(message = "Start date and time must be in the future")
    @NotBlank(message = "End date and time is required")
    private LocalDateTime endDateTime;
}
