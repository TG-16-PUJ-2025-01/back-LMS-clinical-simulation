package co.edu.javeriana.lms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.dtos.CourseDTO;
import co.edu.javeriana.lms.models.Course;
import co.edu.javeriana.lms.repositories.ClassModelRepository;
import co.edu.javeriana.lms.repositories.CourseRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseService {
    
    @Autowired
    private ClassModelRepository classRepository;


    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Course> findAll(Integer page, Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return courseRepository.findAll(pageable); //.map(this::mapToDTO);
    }

    /*private CourseDTO mapToDTO(Course course) {
        return new CourseDTO(
            course.getId(),  // Pass id here
            course.getIdJaveriana(),
            course.getName(),
            course.getCoordinator().getId(),
            course.getCoordinator().getName()
        );
    }*/

        
    public Long countClasses() {
        return classRepository.count();
    }

    public Course findById(Long id) {

        return courseRepository.findById(id).get();
    }
        
    /*private CourseDTO createDTO(Course course) {
        return new CourseDTO(
            course.getId(),  // Pass id here
            course.getIdJaveriana(),
            course.getName(),
            course.getCoordinator().getId(),
            course.getCoordinator().getName()
        );
    }*/
        
    public Course save(CourseDTO course) {

        Course newCourse = new Course(course.getName(), course.getIdJaveriana(), userRepository.findById(course.getCoordinatorId()).get());
        
        
        courseRepository.save(newCourse);
        return newCourse;
    }

    public void deleteById(Long id) {
       
        courseRepository.deleteById(id);
    }

    public Course update(CourseDTO course, Long id) {

        log.info("Updating course with ID: " + id); 

        Course currentCourseModel = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class with ID " + id + " not found"));


        //log.info("Updating course with ID: " + course); 


        // Check for null values before updating
        if (course.getCoordinatorId() == null ||
            course.getName() == null ||
            course.getIdJaveriana() == null ||
            id == null ||
            course.getCoordinatorName() == null
            ) {
            
            throw new IllegalArgumentException("Error: All fields must have values. Null values are not allowed.");
        }

        //log.info("Updating course with ID: " + course); 
        // Update fields
        currentCourseModel.setCoordinator(userRepository.findById(course.getCoordinatorId()).get());
        currentCourseModel.setName(course.getName());
        currentCourseModel.setIdJaveriana(course.getIdJaveriana());

        courseRepository.save(currentCourseModel);

        //log.info("Updating course with ID: " + currentCourseModel); 
        return currentCourseModel;
    }



}
