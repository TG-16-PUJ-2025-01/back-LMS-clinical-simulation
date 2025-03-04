package co.edu.javeriana.lms.subjects.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.accounts.models.User;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    public Course(String name, Long javerianaId, User coordinator) {
        // TODO Auto-generated constructor stub
        this.name = name;
        this.javerianaId = javerianaId;
        this.coordinator = coordinator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(unique = true, nullable = false)
    private Long javerianaId;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClassModel> classModels;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User coordinator;

}