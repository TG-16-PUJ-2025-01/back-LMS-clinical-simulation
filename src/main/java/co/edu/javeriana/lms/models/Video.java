package co.edu.javeriana.lms.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "videos")
@NoArgsConstructor
@RequiredArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long videoId;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    @NonNull
    @Column(nullable = false)
    private Boolean available = true;

    @NonNull
    @Column(nullable = false)
    private Date recordingDate;

    @NonNull
    @Column(nullable = false)
    private Date expirationDate;

    @NonNull
    @Column(nullable = false)
    private Long Duration; // in seconds

    @NonNull
    @Column(nullable = false)
    private Double Size; // in MB

}
