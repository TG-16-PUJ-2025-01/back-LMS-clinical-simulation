package co.edu.javeriana.lms.subjects.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.practices.models.Practice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Classes")
@Data
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class ClassModel {

    public ClassModel(String period, List<User> professor, Course course, Long javerianaId,
            Integer numberOfParticipants) {
        this.period = period;
        this.professors = professor;
        this.course = course;
        this.javerianaId = javerianaId;
        this.numberOfParticipants = numberOfParticipants;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @Column(unique = true, nullable = false)
    private Long javerianaId;

    @Column(nullable = false)
    private String period;

    private Integer numberOfParticipants;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "professor_classes", joinColumns = @JoinColumn(name = "classId"), inverseJoinColumns = @JoinColumn(name = "id"))
    private List<User> professors;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "class_students", joinColumns = @JoinColumn(name = "classId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<User> students;

    @OneToMany(mappedBy = "classModel")
    @JsonIgnore
    private List<Practice> practices;

}