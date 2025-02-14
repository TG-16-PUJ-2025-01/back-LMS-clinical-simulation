package co.edu.javeriana.lms.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ClassModel")
@Data
@NoArgsConstructor
@Getter
public class ClassModel {

    public ClassModel(String name2, Date beginningDate, User professor, Course course, Long idJaveriana) {
        this.name=name2;
        this.beginningDate=beginningDate;
        this.professor=professor;
        this.course=course;
        this.idJaveriana=idJaveriana;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idJaveriana;

    private String name;

    private Date beginningDate;

    @ManyToOne
    private User professor;

    @OneToMany(mappedBy = "classEnrolled")
    @JsonIgnore
    private List<StudentClass> students;

    @ManyToOne
    private Course course;

}