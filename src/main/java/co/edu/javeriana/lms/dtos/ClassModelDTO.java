package co.edu.javeriana.lms.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassModelDTO {
    private Long id;
    private Long javerianaId;
    private String name;
    private String professorName;
    private Long professorId;
    private String courseName;
    private Long courseId;
    private String period;
    private Date beginningDate;
}
