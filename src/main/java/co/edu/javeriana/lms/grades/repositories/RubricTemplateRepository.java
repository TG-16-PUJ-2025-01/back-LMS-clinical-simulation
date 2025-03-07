package co.edu.javeriana.lms.grades.repositories;

import co.edu.javeriana.lms.grades.models.RubricTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RubricTemplateRepository extends JpaRepository<RubricTemplate, Long> {
    
}
