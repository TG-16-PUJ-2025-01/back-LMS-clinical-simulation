package co.edu.javeriana.lms.subjects.models;

import co.edu.javeriana.lms.accounts.models.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "StudentClass")
@Data
@NoArgsConstructor
public class StudentClass {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    private ClassModel classEnrolled;

    @ManyToOne
    private User student;

}
