package co.edu.javeriana.lms.practices.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSimulationRequestDto {
    List<SimulationByTimeSlotDto> simulations;
}
