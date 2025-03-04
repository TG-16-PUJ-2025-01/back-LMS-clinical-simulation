package co.edu.javeriana.lms.practices.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.grades.models.GradeStatus;
import co.edu.javeriana.lms.videos.models.Video;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "simulations")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long simulationId;

    @Column(nullable = false)
    private Float grade = 0.0f;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeStatus gradeStatus;

    @NonNull
    @Column(nullable = false)
    private Date gradeDate;

    @OneToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @OneToMany(mappedBy = "simulation")
    @JsonIgnore
    private List<GroupPerSimulation> groups;
}
