package co.edu.javeriana.lms.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.dtos.ClassModelDTO;
import co.edu.javeriana.lms.dtos.CourseDTO;
import co.edu.javeriana.lms.models.ClassModel;
import co.edu.javeriana.lms.models.Course;
import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.repositories.ClassModelRepository;
import co.edu.javeriana.lms.repositories.CourseRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CourseService implements CrudService<CourseDTO, Long>{
    
    @Autowired
    private ClassModelRepository classRepository;


    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CourseDTO> findAll(Integer page, Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return createDTOsList(courseRepository.findAll(pageable).getContent());
    }

    private List<CourseDTO> createDTOsList(List<Course> courses) {
        return courses.stream()
        .map(course -> new CourseDTO(
            course.getId(),  // Pass id here
            course.getIdJaveriana(),
            course.getName(),
            course.getCoordinator().getId(),
            course.getCoordinator().getName()
        ))
        .toList();    
    }

        
    public Long countClasses() {
        return classRepository.count();
    }

    public CourseDTO findById(Long id) {

        return createDTO(courseRepository.findById(id).get());
    }
        
    private CourseDTO createDTO(Course course) {
        return new CourseDTO(
            course.getId(),  // Pass id here
            course.getIdJaveriana(),
            course.getName(),
            course.getCoordinator().getId(),
            course.getCoordinator().getName()
        );
    }
        
    public CourseDTO save(CourseDTO course) {
        Course newCourse = new Course(course.getId(), course.getName(), course.getIdJaveriana(), userRepository.findById(course.getCoordinatorId()).get());
        courseRepository.save(newCourse);
        return course;
    }

    public void deleteById(Long id) {
        classRepository.deleteById(id);
    }

    public void update(CourseDTO course) {
        Course currentCourseModel = courseRepository.findById(course.getId())
                .orElseThrow(() -> new EntityNotFoundException("Class with ID " + course.getId() + " not found"));

        // Check for null values before updating
        if (course.getCoordinatorId() == null ||
            course.getName() == null ||
            course.getIdJaveriana() == null ||
            course.getId() == null ||
            course.getCoordinatorName() == null
            ) {
            
            throw new IllegalArgumentException("Error: All fields must have values. Null values are not allowed.");
        }

        // Update fields
        currentCourseModel.setCoordinator(userRepository.findById(course.getCoordinatorId()).get());
        currentCourseModel.setName(course.getName());
        currentCourseModel.setIdJaveriana(course.getIdJaveriana());

        courseRepository.save(currentCourseModel);
    }



}
