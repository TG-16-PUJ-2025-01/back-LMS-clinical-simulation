package co.edu.javeriana.lms.videos.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.videos.models.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Video> findByName(String name);
}
