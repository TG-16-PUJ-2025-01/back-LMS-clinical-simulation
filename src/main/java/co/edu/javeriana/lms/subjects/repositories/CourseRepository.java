package co.edu.javeriana.lms.subjects.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.subjects.models.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("""
                SELECT c FROM Course c
                WHERE CAST(c.javerianaId AS string) LIKE %:filter%
                OR LOWER(TRANSLATE(c.name, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
                OR LOWER(TRANSLATE(c.coordinator.name, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
                OR LOWER(TRANSLATE(c.coordinator.lastName, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
                OR LOWER(TRANSLATE(c.faculty, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
                OR LOWER(TRANSLATE(c.department, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
                OR LOWER(TRANSLATE(c.program, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
                OR CAST(c.semester AS string) LIKE %:filter%
            """)
    Page<Course> findByNameOrJaverianaIdContaining(@Param("filter") String filter, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.coordinator = :coordinator")
    // buscar con el filtro por el id del curso, el nombre del curso

    List<Course> findCoursesByCoordinator(@Param("coordinator") User coordinator);

    @Query("SELECT c FROM Course c WHERE c.coordinator = :coordinator AND (CAST(c.javerianaId AS string) LIKE %:filter% OR LOWER(TRANSLATE(c.name, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%')))")
    List<Course> findCoursesByCoordinatorAndNameContaining(User coordinator, @Param("filter") String filter);

    @Query("SELECT rt FROM Course c JOIN c.rubricTemplates rt WHERE c.courseId = :courseId AND LOWER(rt.title) LIKE LOWER(CONCAT('%', :title, '%')) AND rt.archived = false ORDER BY rt.creationDate ASC")
    Page<RubricTemplate> findByCourseIdAndTitleContainingIgnoreCase(
            @Param("courseId") Long courseId,
            @Param("title") String title,
            Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.javerianaId = :courseId")
    Optional<Course> findByJaverianaId( @Param("courseId") Long courseId);
}
