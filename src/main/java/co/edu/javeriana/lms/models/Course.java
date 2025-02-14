package co.edu.javeriana.lms.models;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Course")
@Data
@NoArgsConstructor
public class Course {

    public Course(Long id, String name, Long idJaveriana, User coordinator) {
        //TODO Auto-generated constructor stub
        this.id = id;
        this.name = name;
        this.idJaveriana = idJaveriana;
        this.coordinator = coordinator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private Long idJaveriana;

    @Column(unique=true, nullable=false)
    private String name;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<ClassModel> classModels;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private User coordinator;

}