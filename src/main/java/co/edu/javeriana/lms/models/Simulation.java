package co.edu.javeriana.lms.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "simulations")
@NoArgsConstructor
@RequiredArgsConstructor
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long simulationId;

    @NonNull
    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Float grade = 0.0f;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeStatus gradeStatus;

    @NonNull
    @Column(nullable = false)
    private Date gradeDate;

    @NonNull
    @Column(nullable = false, unique = true)
    private String videoName;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus videoStatus;

    @NonNull
    @Column(nullable = false)
    private Date videoExpirationDate;

    @NonNull
    @Column(nullable = false)
    private Long videoDuration; // in seconds

    @NonNull
    @Column(nullable = false)
    private Double videoSize; // in MB
}
