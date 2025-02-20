package co.edu.javeriana.lms.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.models.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByNameContaining(String name, Pageable pageable);
}
