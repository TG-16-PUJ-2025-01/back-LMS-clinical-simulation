package co.edu.javeriana.lms.subjects.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
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

        return courseRepository.findById(id).get();
    }

    public Course save(CourseDto course) {
        Optional<User> coordinator = userRepository.findById(course.getCoordinatorId());

        if (coordinator.isEmpty()
                || coordinator.get().getRoles().stream().noneMatch(role -> role.equals(Role.COORDINADOR))) {
            throw new EntityNotFoundException("Coordinator with ID " + course.getCoordinatorId() + " not found");
        }

        Course newCourse = new Course(course.getName(), course.getJaverianaId(),
                userRepository.findById(course.getCoordinatorId()).get(), course.getFaculty(), course.getDepartment(),
                course.getProgram(), course.getSemester());

        courseRepository.save(newCourse);
        return newCourse;
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    public Course update(CourseDto course, Long id) {

        log.info("Updating course with ID: " + id);

        Course currentCourseModel = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + id + " not found"));

        currentCourseModel.setName(course.getName());
        currentCourseModel.setJaverianaId(course.getJaverianaId());
        currentCourseModel.setCoordinator(userRepository.findById(course.getCoordinatorId()).get());

        courseRepository.save(currentCourseModel);

        // log.info("Updating course with ID: " + currentCourseModel);
        return currentCourseModel;
    }

    public List<CourseDto> findAllCoordinatorCourses(String filter, String sort, Boolean asc, String email,
            String searsearchByKey, String period) {
        User coordinator = userRepository.findByEmail(email).get();

        // Obtener los cursos del coordinador

        List<Course> coordinatorCourses = new ArrayList<>();

        log.info("Searching by key: " + searsearchByKey + " and filter: " + filter+ " and period: " + period);

        if (searsearchByKey.isEmpty() || searsearchByKey.equals("Clases") || searsearchByKey.equals("Profesores")) {
            coordinatorCourses = courseRepository.findCoursesByCoordinator(coordinator);
        } else {
            coordinatorCourses = courseRepository.findCoursesByCoordinatorAndNameContaining(coordinator, filter);
        }

        List<CourseDto> courses = new ArrayList<>();

        for (Course course : coordinatorCourses) {

            List<ClassModel> sortedClasses = new ArrayList<>();
            // Buscar las clases del curs o y ordenarlas por periodo de mayor a menor
            if (searsearchByKey.isEmpty() || searsearchByKey.equals("Asignaturas")) {
                sortedClasses = classRepository.findClassesByCourseId(course, period); // Convertir el stream en lista

            } else if (searsearchByKey.equals("Clases")) {
                sortedClasses = classRepository.findClassesByCourseIdAndNameContaining(course, filter, period); // Convertir el                                                                                          // lista
            } else {
                sortedClasses = classRepository.findClassesByCourseByProfessorContaining(course, filter,period); // Convertir
                                                                                                          // el stream                                                                                             // en lista
            }

            // Crear un CourseDto con las clases ordenadas
            courses.add(new CourseDto(course.getCourseId(), course.getJaverianaId(), course.getName(),
                    course.getCoordinator().getId(), sortedClasses.stream()
                            .sorted((c1, c2) -> c2.getPeriod().compareTo(c1.getPeriod())) // Ordenar por periodo (mayor                                                          // a menor)
                            .toList(), course.getFaculty(), course.getDepartment(), course.getProgram(),
                    course.getSemester()));

        }

        // log.info("Courses found: " + courses.toString());

        // enviar el resultado de mayor a menos numero de clases encontradas
        return courses.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getClasses().size(), c1.getClasses().size())) // Ordenar por
                                                                                                     // periodo (mayor a
                                                                                                     // menor)
                .toList();
    }

}
