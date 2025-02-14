package co.edu.javeriana.lms.dtos;

import java.util.Date;

import co.edu.javeriana.lms.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ClassModelDTO {
   
    private Long id;
    private Long idJaveriana;
    private String name;
    private String professorName;
    private Long professorId;
    private String courseName;
    private Long courseId;
    private String period;
    private Date beginningDate;

    // Constructor
    public ClassModelDTO(Long id, Long idJaveriana, String name, String professorName, 
                     Long professorId, String courseName, Long courseId, 
                     String period, java.util.Date beginningDate) {
        this.id = id;
        this.idJaveriana = idJaveriana;
        this.name = name;
        this.professorName = professorName;
        this.professorId = professorId;
        this.courseName = courseName;
        this.courseId = courseId;
        this.period = period;
        this.beginningDate = beginningDate;
    }

}
