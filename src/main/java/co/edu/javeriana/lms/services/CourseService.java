package co.edu.javeriana.lms.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.dtos.CreateCourseDTO;
import co.edu.javeriana.lms.dtos.EditCourseDTO;
import co.edu.javeriana.lms.models.Course;
import co.edu.javeriana.lms.models.Role;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.repositories.ClassRepository;
import co.edu.javeriana.lms.repositories.CourseRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Course> findAll(String filter, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        if (filter == "coordinator") {
            sortOrder = asc ? Sort.by("coordinator.name", "coordinator.lastName").ascending()
                    : Sort.by("coordinator.name", "coordinator.lastName").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return courseRepository.findByNameOrJaverianaIdContaining(filter, pageable);
    }

    public Long countClasses() {
        return classRepository.count();
    }

    public Course findById(Long id) {

        return courseRepository.findById(id).get();
    }

    public Course save(CreateCourseDTO course) {
        Optional<User> coordinator = userRepository.findById(course.getCoordinatorId());

        if (coordinator.isEmpty()
                || coordinator.get().getRoles().stream().noneMatch(role -> role.equals(Role.COORDINADOR))) {
            throw new EntityNotFoundException("Coordinator with ID " + course.getCoordinatorId() + " not found");
        }

        Course newCourse = new Course(course.getName(), course.getJaverianaId(),
                userRepository.findById(course.getCoordinatorId()).get());

        courseRepository.save(newCourse);
        return newCourse;
    }

    public void deleteById(Long id) {

        courseRepository.deleteById(id);
    }

    public Course update(EditCourseDTO course, Long id) {

        log.info("Updating course with ID: " + id);

        Course currentCourseModel = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + id + " not found"));

        currentCourseModel.setName(course.getName());
        currentCourseModel.setJaverianaId(course.getJaverianaId());

        courseRepository.save(currentCourseModel);

        // log.info("Updating course with ID: " + currentCourseModel);
        return currentCourseModel;
    }

}
