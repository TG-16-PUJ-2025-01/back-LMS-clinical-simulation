package co.edu.javeriana.lms.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.subjects.dtos.CourseDto;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
import co.edu.javeriana.lms.subjects.services.CourseService;
import jakarta.persistence.EntityNotFoundException;

public class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private User mockCoordinator;
    private Course mockCourse;
    private CourseDto mockCourseDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCoordinator = new User();
        mockCoordinator.setId(1L);
        mockCoordinator.setName("John");
        mockCoordinator.setLastName("Doe");
        mockCoordinator.setRoles(Set.of(Role.COORDINADOR));

        mockCourse = new Course("Mathematics", 101L, mockCoordinator);
        mockCourse.setCourseId(1L);

        mockCourseDto = new CourseDto();
        mockCourseDto.setName("Mathematics");
        mockCourseDto.setJaverianaId(101L);
        // TODO cambiarlo por String cuando se haga el cambio
        mockCourseDto.setCoordinatorId(1L); 
    }

    @Test
    public void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Course> mockPage = new PageImpl<>(List.of(mockCourse), pageable, 1);

        when(courseRepository.findByNameOrJaverianaIdContaining(anyString(), any(Pageable.class))).thenReturn(mockPage);

        Page<Course> result = courseService.findAll("Math", 0, 10, "name", true);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockCourse, result.getContent().get(0));
    }

    @Test
    public void testFindById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));

        Course result = courseService.findById(1L);

        assertNotNull(result);
        assertEquals(mockCourse, result);
    }

    @Test
    public void testFindByIdNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.findById(1L);
        });
    }

    @Test
    public void testSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockCoordinator));
        when(courseRepository.save(any(Course.class))).thenReturn(mockCourse);

        Course result = courseService.save(mockCourseDto);

        assertNotNull(result);
        assertEquals(mockCourse.getName(), result.getName());
        assertEquals(mockCourse.getJaverianaId(), result.getJaverianaId());
        assertEquals(mockCourse.getCoordinator(), result.getCoordinator());
    }

    @Test
    public void testSaveCoordinatorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.save(mockCourseDto);
        });
    }

    @Test
    public void testSaveCoordinatorNotValidRole() {
        User invalidCoordinator = new User();
        invalidCoordinator.setId(2L);
        invalidCoordinator.setRoles(Set.of(Role.PROFESOR));

        when(userRepository.findById(2L)).thenReturn(Optional.of(invalidCoordinator));

        mockCourseDto.setCoordinatorId(2L);

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.save(mockCourseDto);
        });
    }

    @Test
    public void testDeleteById() {
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteById(1L);

        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdate() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockCoordinator));
        when(courseRepository.save(any(Course.class))).thenReturn(mockCourse);

        Course result = courseService.update(mockCourseDto, 1L);

        assertNotNull(result);
        assertEquals(mockCourse.getName(), result.getName());
        assertEquals(mockCourse.getJaverianaId(), result.getJaverianaId());
        assertEquals(mockCourse.getCoordinator(), result.getCoordinator());
    }

    @Test
    public void testUpdateCourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.update(mockCourseDto, 1L);
        });
    }

    @Test
    public void testUpdateCoordinatorNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.update(mockCourseDto, 1L);
        });
    }
}