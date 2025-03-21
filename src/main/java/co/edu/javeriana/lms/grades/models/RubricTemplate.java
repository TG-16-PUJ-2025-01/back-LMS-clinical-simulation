package co.edu.javeriana.lms.grades.models;

import java.sql.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.subjects.models.Course;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Rubric_Template")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RubricTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rubricTemplateId;

    @Column(unique = true, nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") 
    private List<Criteria> criteria;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") 
    private List<RubricColumn> columns;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date creationDate;

    @Column(nullable = false)
    private Boolean archived;

    @ManyToMany
    @JoinTable(name = "Rubric_Template_Course", joinColumns = @JoinColumn(name = "rubricTemplateId"), inverseJoinColumns = @JoinColumn(name = "courseId"))  
    @JsonIgnore
    private List<Course> courses;

    //no se deberian borrar las practicas si se borra la rubrica
    //revisar que poner
    @OneToOne
    @JsonIgnore
    private Practice practice;

    @OneToMany(mappedBy = "rubricTemplate", cascade = CascadeType.DETACH, orphanRemoval = false)    @JsonIgnore
    private List<Rubric> rubrics;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User creator;
    
}
