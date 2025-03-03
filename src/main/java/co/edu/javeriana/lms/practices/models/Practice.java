package co.edu.javeriana.lms.practices.models;

import java.util.List;

import co.edu.javeriana.lms.booking.models.TimeSlot;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "practice")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Practice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String description;

    @NonNull
    @Column(nullable = false)
    private Boolean gradeable;

    // TODO: Could be nullable to indicate that the practice is individual
    @NonNull
    @Column(nullable = true)
    private Integer numberOfGroups;

    // TODO: Could be nullable to indicate that the practice is individual
    @NonNull
    @Column(nullable = true)
    private Integer maxStudentsGroup;

    // TODO: Relation with TimeSlot
    /*
     * @NonNull
     * 
     * @OneToMany(mappedBy = "practice")
     * private List<TimeSlot> timeSlot;
     */

    // TODO: Relation with ClassModel
    /*
     * @NonNull
     * 
     * @ManyToOne
     * 
     * @JoinColumn(nullable = false)
     * private ClassModel classModel;
     */

    // TODO: Relation with Simulation
    // Need to implement logic to create simulations groups
    /*
     * @NonNull
     * 
     * @OneToMany(mappedBy = "practice")
     * private List<Simulation> simulations;
     */

}
