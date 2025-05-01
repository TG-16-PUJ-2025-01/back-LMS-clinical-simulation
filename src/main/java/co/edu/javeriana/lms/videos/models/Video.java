package co.edu.javeriana.lms.videos.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.practices.models.Simulation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "videos")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long videoId;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @NonNull
    @Column(nullable = false)
    private Date recordingDate;

    @NonNull
    @Column(nullable = false)
    private Date expirationDate;

    @NonNull
    @Column(nullable = false)
    private Long duration; // in seconds

    @NonNull
    @Column(nullable = false)
    private Double size; // in MB

    @ManyToOne
    @JoinColumn(name = "simulation_id")
    @JsonIgnore
    private Simulation simulation;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("timestamp ASC") 
    private List<Comment> comments;

}
