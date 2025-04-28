package co.edu.javeriana.lms.booking.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.practices.models.Simulation;
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
import jakarta.persistence.UniqueConstraint;
import lombok.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "room", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    @NonNull
    @Column(nullable = false)
    private Integer capacity;

    @NonNull
    @Column(nullable = false)
    private String ip;

    @ManyToOne
    @NonNull
    private RoomType type;

    @ManyToMany
    @JoinTable(name = "simulation_rooms", joinColumns = @JoinColumn(name = "simulationId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<Simulation> simulations;

}
