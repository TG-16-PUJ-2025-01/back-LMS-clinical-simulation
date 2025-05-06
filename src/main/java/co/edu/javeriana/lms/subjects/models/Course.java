package co.edu.javeriana.lms.subjects.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    public Course(String name, Long javerianaId, User coordinator, String faculty, String department, String program,
            Integer semester) {
        this.name = name;
        this.javerianaId = javerianaId;
        this.coordinator = coordinator;
        this.faculty = faculty;
        this.department = department;
        this.program = program;
        this.semester = semester;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(unique = true, nullable = false)
    private Long javerianaId;

    @Column(unique = true, nullable = false)
    private String name;

    private String faculty;

    private String department;

    private String program;

    private Integer semester;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClassModel> classModels;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User coordinator;

    @ManyToMany
    @JoinTable(name = "Rubric_Template_Course", joinColumns = @JoinColumn(name = "courseId"), inverseJoinColumns = @JoinColumn(name = "rubricTemplateId"))
    private List<RubricTemplate> rubricTemplates;
}