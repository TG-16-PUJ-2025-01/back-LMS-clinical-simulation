package co.edu.javeriana.lms.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
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

    @NonNull
    @Column(nullable = false)
    private String maxStudentsGroup;

    @NonNull
    @OneToMany(mappedBy = "practice")
    private List<TimeSlot> timeSlot;

    //TODO: Missing attribute for practice type?
    
}
