package co.edu.javeriana.lms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.javeriana.lms.models.Course;

public interface CourseRepository extends JpaRepository <Course, Long> {
    @Query("""
        SELECT c FROM Course c 
        WHERE CAST(c.javerianaId AS string) LIKE %:filter% 
        OR LOWER(TRANSLATE(c.name, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
    """)
    Page<Course> findByNameOrJaverianaIdContaining(@Param("filter") String filter, Pageable pageable);

}
