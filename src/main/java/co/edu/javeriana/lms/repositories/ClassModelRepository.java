package co.edu.javeriana.lms.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.lms.models.ClassModel;

@Repository
public interface ClassModelRepository extends JpaRepository <ClassModel, Long> {
    
}
