package co.edu.javeriana.lms.grades.repositories;

import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.subjects.models.Course;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RubricTemplateRepository extends JpaRepository<RubricTemplate, Long> {
    
    @Query("""
        SELECT c FROM RubricTemplate c 
        WHERE 
            LOWER(TRANSLATE(c.title, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
            OR CAST(c.creationDate AS string) LIKE CONCAT('%', :filter, '%')
        """)
    Page<RubricTemplate> findByTitleOrCreationDateContaining(@Param("filter") String filter, Pageable pageable);
    
}
