package co.edu.javeriana.lms.practices.dtos;

import java.util.Date;

import co.edu.javeriana.lms.grades.models.GradeStatus;
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
public class SimulationDto {
    @NotNull(message = "Practice ID is required")
    private Long practiceId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Start date and time is required")
    private Date startDateTime;

    @NotNull(message = "End date and time is required")
    private Date endDateTime;

    private Float grade;

    private GradeStatus gradeStatus;

    private Date gradeDateTime;
}
