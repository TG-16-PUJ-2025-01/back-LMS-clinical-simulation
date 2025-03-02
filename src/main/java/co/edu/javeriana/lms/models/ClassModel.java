package co.edu.javeriana.lms.models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Classes")
@Data
@NoArgsConstructor
@Getter
public class ClassModel {

    public ClassModel(String period, List<User> professor, Course course, Long javerianaId) {
        this.period = period;
        this.professors = professor;
        this.course = course;
        this.javerianaId = javerianaId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @Column(unique = true, nullable = false)
    private Long javerianaId;

    @Column(nullable = false)
    private String period;

    @ManyToMany
    @JoinTable(name = "professor_classes", joinColumns = @JoinColumn(name = "classId"), inverseJoinColumns = @JoinColumn(name = "id"))  
    private List<User> professors;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToMany
    @JoinTable(name = "class_students", joinColumns = @JoinColumn(name = "classId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<User> students;

}