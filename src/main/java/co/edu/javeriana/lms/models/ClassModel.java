package co.edu.javeriana.lms.models;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    public ClassModel(String name2, Date beginningDate, User professor, Course course, Long javerianaId) {
        this.name = name2;
        this.beginningDate = beginningDate;
        this.professor = professor;
        this.course = course;
        this.javerianaId = javerianaId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long javerianaId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date beginningDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User professor;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "classEnrolled", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<StudentClass> students;

}