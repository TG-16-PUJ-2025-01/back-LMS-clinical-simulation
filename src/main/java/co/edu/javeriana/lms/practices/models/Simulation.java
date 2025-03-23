package co.edu.javeriana.lms.practices.models;

import java.util.Date;
import java.util.List;

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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(nullable = false)
    private Date startDateTime;

    @Column(nullable = false)
    private Date endDateTime;

    @Column(nullable = true)
    private Float grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private GradeStatus gradeStatus;

    @Column(nullable = true)
    private Date gradeDateTime;

    @ManyToOne
    @JsonIgnore
    private Practice practice;

    @OneToOne
    @JoinColumn(name = "video_id")
    private Video video;

    /*@ManyToMany
    @JoinTable(name = "simulation_users", joinColumns = @JoinColumn(name = "simulationId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore

    private List<GroupPerSimulation> groups;*/

    @OneToOne
    @JoinColumn(nullable = true)
    private Rubric rubric;

    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room;

}
