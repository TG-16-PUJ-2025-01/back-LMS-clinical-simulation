package co.edu.javeriana.lms.practices.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.subjects.models.ClassModel;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PracticeType type;

    @NonNull
    @Column(nullable = false)
    private Boolean gradeable;

    @Column(nullable = false)
    private Integer simulationDuration;

    @Nullable
    @Column(nullable = true)
    private Integer numberOfGroups;

    @Nullable
    @Column(nullable = true)
    private Integer maxStudentsGroup;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private ClassModel classModel;

    @OneToMany(mappedBy = "practice")
    @JsonIgnore
    private List<Simulation> simulations;
}
