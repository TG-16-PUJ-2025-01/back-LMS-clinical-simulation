package co.edu.javeriana.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {

    Long id;
    Long idJaveriana;
    String name;
    Long coordinatorId;
    String coordinatorName;

    public CourseDTO(Long idJaveriana, String name, Long coordinatorId, String coordinatorName) {
        this.idJaveriana = idJaveriana;
        this.name = name;
        this.coordinatorId = coordinatorId;
        this.coordinatorName = coordinatorName;
    }

    
}
