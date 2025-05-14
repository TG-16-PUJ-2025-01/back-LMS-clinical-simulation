package co.edu.javeriana.lms.subjects.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.subjects.dtos.CourseDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
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
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + id + " not found"));
    }

    public Course save(CourseDto course) {
        // Check if the JaverianaId already exists
        boolean javerianaIdExists = courseRepository.findByJaverianaId(course.getJaverianaId()).isPresent();
        if (javerianaIdExists) {
            throw new EntityNotFoundException("Course with javeriana ID " + course.getJaverianaId() + " already exists");
        }

        User coordinator = userRepository.findById(course.getCoordinatorId()).orElseThrow(() -> new EntityNotFoundException(
                "Coordinator with ID " + course.getCoordinatorId() + " not found"));

        Course newCourse = new Course(course.getName(), course.getJaverianaId(),
                coordinator, course.getFaculty(), course.getDepartment(),
                course.getProgram(), course.getSemester());

        courseRepository.save(newCourse);
        return newCourse;
    }

    public void deleteById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }

        courseRepository.deleteById(id);
    }

    public Course update(CourseDto course, Long id) {

        log.info("Updating course with ID: " + id);

        Course currentCourseModel = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + id + " not found"));

        // Check if the JaverianaId is being updated and already exists in another course
        if (!currentCourseModel.getJaverianaId().equals(course.getJaverianaId())) {
            boolean javerianaIdExists = courseRepository.findByJaverianaId(course.getJaverianaId())
                    .filter(existingCourse -> !existingCourse.getCourseId().equals(id))
                    .isPresent();

            if (javerianaIdExists) {
                throw new EntityNotFoundException("Javeriana ID " + course.getJaverianaId() + " already exists");
            }
        }

        // Check if the coordinator exists
        User coordinator = userRepository.findById(course.getCoordinatorId())
                .orElseThrow(() -> new EntityNotFoundException("Coordinator with ID " + course.getCoordinatorId() + " not found"));

        currentCourseModel.setName(course.getName());
        currentCourseModel.setJaverianaId(course.getJaverianaId());
        currentCourseModel.setCoordinator(coordinator);
        currentCourseModel.setFaculty(course.getFaculty());
        currentCourseModel.setDepartment(course.getDepartment());
        currentCourseModel.setProgram(course.getProgram());
        currentCourseModel.setSemester(course.getSemester());

        courseRepository.save(currentCourseModel);

        return currentCourseModel;
    }

    public List<CourseDto> findAllCoordinatorCourses(String filter, String sort, Boolean asc, String email,
            String searsearchByKey, String period) {
        User coordinator = userRepository.findByEmail(email).get();

        List<Course> coordinatorCourses = new ArrayList<>();

        log.info("Searching by key: " + searsearchByKey + " and filter: " + filter + " and period: " + period);

        if (searsearchByKey.isEmpty() || searsearchByKey.equals("Clases") || searsearchByKey.equals("Profesores")) {
            coordinatorCourses = courseRepository.findCoursesByCoordinator(coordinator);
        } else {
            coordinatorCourses = courseRepository.findCoursesByCoordinatorAndNameContaining(coordinator, filter);
        }

        List<CourseDto> courses = new ArrayList<>();

        for (Course course : coordinatorCourses) {

            List<ClassModel> sortedClasses = new ArrayList<>();
            if (searsearchByKey.isEmpty() || searsearchByKey.equals("Asignaturas")) {
                sortedClasses = classRepository.findClassesByCourseId(course, period);

            } else if (searsearchByKey.equals("Clases")) {
                sortedClasses = classRepository.findClassesByCourseIdAndNameContaining(course, filter, period);
            } else {
                sortedClasses = classRepository.findClassesByCourseByProfessorContaining(course, filter, period);
            }

            courses.add(new CourseDto(course.getCourseId(), course.getJaverianaId(), course.getName(),
                    course.getCoordinator().getId(), sortedClasses.stream()
                            .sorted((c1, c2) -> c2.getPeriod().compareTo(c1.getPeriod()))
                            .toList(),
                    course.getFaculty(), course.getDepartment(), course.getProgram(),
                    course.getSemester()));

        }

        return courses.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getClasses().size(), c1.getClasses().size()))
                .toList();
    }

    public Page<RubricTemplate> findRecommendedRubricsByCourseId(Long courseId, String filter, Integer page,
            Integer size,
            String sort, Boolean asc) {
        Pageable pageable = PageRequest.of(page, size);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + courseId + " not found"));

        return courseRepository.findByCourseIdAndTitleContainingIgnoreCase(course.getCourseId(), filter,
                pageable);
    }
}
