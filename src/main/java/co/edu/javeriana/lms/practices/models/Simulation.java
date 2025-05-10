package co.edu.javeriana.lms.practices.models;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.grades.models.GradeStatus;
import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.videos.models.Video;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "simulation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long simulationId;

    @Column(nullable = true)
    private Date startDateTime;

    @Column(nullable = true)
    private Date endDateTime;

    @Column(nullable = true)
    private Float grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private GradeStatus gradeStatus;

    @Column(nullable = true)
    private Date gradeDateTime;

    @Column(nullable = false)
    private Integer groupNumber;

    @ManyToOne
    private Practice practice;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "simulation_id")
    private List<Video> videos;

    @OneToOne
    @JoinColumn(nullable = true)
    private Rubric rubric;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "simulation_users", joinColumns = @JoinColumn(name = "simulationId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "simulation_rooms", joinColumns = @JoinColumn(name = "simulationId"), inverseJoinColumns = @JoinColumn(name = "id"))
    private List<Room> rooms;
}
