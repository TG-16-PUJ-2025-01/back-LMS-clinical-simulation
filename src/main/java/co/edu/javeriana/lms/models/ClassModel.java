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

    public ClassModel(String name2, Date beginningDate, List<User> professor, Course course, Long javerianaId) {
        this.name = name2;
        this.beginningDate = beginningDate;
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
    private String name;

    @Column(nullable = false)
    private Date beginningDate;

    @ManyToMany
    @JoinTable(name = "professor_classes", joinColumns = @JoinColumn(name = "classId"), inverseJoinColumns = @JoinColumn(name = "id"))  
    private List<User> professors;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "class_students", joinColumns = @JoinColumn(name = "classId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<User> students;

    public String getPeriod() {

        if (beginningDate == null) {
            throw new IllegalArgumentException("Beginning date cannot be null");
        }

        //log.info("REVISAR BIEN UNICORNIO" + beginningDate.toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginningDate);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = calendar.get(Calendar.YEAR);

        if (month >= 1 && month < 5) { // January - February
            return year + "-10";
        } else if (month >= 5 && month < 9) { // May - June
            return year + "-20";
        } else if (month >= 9 && month <= 12) { // September - October
            return year + "-30";
        }

        throw new IllegalArgumentException("Date does not match any academic period");
    }


}