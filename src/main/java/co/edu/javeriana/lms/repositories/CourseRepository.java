package co.edu.javeriana.lms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.lms.models.Course;

public interface CourseRepository extends JpaRepository <Course, Long> {
    
}
