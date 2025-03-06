package co.edu.javeriana.lms.grades.models;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.query.spi.Limit;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.subjects.models.ClassModel;
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
import jakarta.persistence.OneToMany;
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

    @ManyToMany
    @JoinTable(name = "Rubric_Template_Course", joinColumns = @JoinColumn(name = "rubricTemplateId"), inverseJoinColumns = @JoinColumn(name = "courseId"))  
    private List<Course> courses;

    //no se deberian borrar las practicas si se borra la rubrica
    //revisar que poner
    @OneToMany(mappedBy = "rubricTemplate")
    @JsonIgnore
    private List<Practice> practices;

    @OneToMany(mappedBy = "rubricTemplate")
    @JsonIgnore
    private List<Rubric> rubrics;
}
