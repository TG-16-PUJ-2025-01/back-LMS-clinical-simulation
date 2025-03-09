package co.edu.javeriana.lms.grades.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Rubric_Template")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RubricTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rubricTemplateId;

    @Column(unique = true, nullable = false)
    private String title;
    
}
