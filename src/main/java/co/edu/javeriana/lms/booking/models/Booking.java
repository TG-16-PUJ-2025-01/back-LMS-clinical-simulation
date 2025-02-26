package co.edu.javeriana.lms.booking.models;

import java.time.LocalDateTime;

import co.edu.javeriana.lms.practices.models.GroupPerSimulation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "booking")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NonNull
    @Column(nullable = false)
    private LocalDateTime finishDate;

    @NonNull
    @ManyToOne
    private Room room;

    @NonNull
    @ManyToOne
    private GroupPerSimulation groupPerSimulation;

}
