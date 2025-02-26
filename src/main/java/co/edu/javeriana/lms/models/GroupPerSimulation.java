package co.edu.javeriana.lms.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "group_per_simulation")
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class GroupPerSimulation {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    private Simulation simulation;

    @NonNull
    @ManyToOne
    private User user;

    @NonNull
    @OneToMany(mappedBy = "groupPerSimulation")
    private List<Booking> bookings;
    
}
