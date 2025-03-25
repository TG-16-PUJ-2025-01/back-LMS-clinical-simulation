package co.edu.javeriana.lms.grades.repositories;

import co.edu.javeriana.lms.grades.models.Rubric;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RubricRepository extends JpaRepository<Rubric, Long> {
    
}
