package co.edu.javeriana.lms.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import co.edu.javeriana.lms.subjects.dtos.CourseDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
import co.edu.javeriana.lms.subjects.services.CourseService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
public class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private RubricRepository rubricRepository;

    @Mock
    private UserRepository userRepository;

    private static User mockCoordinator;
    private static Course mockCourse1;
    private static Course mockCourse2;
    private static RubricTemplate mockRubricTemplate;
    private static ClassModel mockClass1;
    private static ClassModel mockClass2;
    private static Page<Course> mockCoursesPage;
    private static List<ClassModel> mockClassModels;
    private static CourseDto mockCourseDto;

    @BeforeAll
    public static void setUpAll() {
        // Mock Coordinator
        mockCoordinator = User.builder()
                .id(1L)
                .email("alice.johnson@example.com")
                .password("password123")
                .roles(new HashSet<>(Set.of(Role.COORDINADOR)))
                .preferredRole(Role.COORDINADOR)
                .name("Alice")
                .lastName("Johnson")
                .institutionalId("00032313")
                .build();

        // Mock Courses
        mockCourse1 = Course.builder()
                .courseId(1L)
                .javerianaId(20001L)
                .name("Cirugia General")
                .faculty("Medicina")
                .department("Cirugia")
                .program("Medicina")
                .semester(5)
                .build();

        mockCourse2 = Course.builder()
                .courseId(2L)
                .javerianaId(20002L)
                .name("Cirugia Plastica")
                .faculty("Medicina")
                .department("Cirugia")
                .program("Medicina")
                .semester(6)
                .build();

        // Mock Course DTO
        mockCourseDto = CourseDto.builder()
                .courseId(3L)
                .javerianaId(20003L)
                .name("Cirugia Cardiaca")
                .coordinatorId(1L)
                .faculty("Medicina")
                .department("Cirugia")

                .program("Medicina")
                .semester(7)
                .build();

        // Mock RubricTemplate
        mockRubricTemplate = RubricTemplate.builder()
                .rubricTemplateId(1L)
                .title("Rubrica de Evaluación de Cirugía")
                .criteria(List.of())
                .columns(List.of())
                .creationDate(new Date())
                .archived(false)
                .build();

        // Mock Classes
        mockClass1 = ClassModel.builder()
                .classId(1L)
                .javerianaId(20001L)
                .period("2025-10")
                .numberOfParticipants(20)
                .build();

        mockClass2 = ClassModel.builder()
                .classId(2L)
                .javerianaId(20002L)
                .period("2025-30")
                .numberOfParticipants(25)
                .build();

        // Set Relationships
        mockCourse1.setClassModels(List.of(mockClass1, mockClass2));
        mockCourse1.setCoordinator(mockCoordinator);
        mockCourse1.setRubricTemplates(List.of(mockRubricTemplate));

        mockCourse2.setClassModels(List.of(mockClass1));
        mockCourse2.setCoordinator(mockCoordinator);
        mockCourse2.setRubricTemplates(List.of(mockRubricTemplate));

        mockCourseDto.setClasses(List.of(mockClass1, mockClass2));

        mockClassModels = Arrays.asList(mockClass1, mockClass2);

        mockCoursesPage = new PageImpl<>(Arrays.asList(mockCourse1, mockCourse2),
                PageRequest.of(0, 10, Sort.by("courseId").ascending()), mockClassModels.size());
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchAllCourses() {
        // Arrange
        String filter = "Cirugia";
        int page = 0;
        int size = 10;
        String sort = "courseId";
        boolean asc = true;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        // Mock the repository method
        when(courseRepository.findByNameOrJaverianaIdContaining(filter, pageable)).thenReturn(mockCoursesPage);

        // Act
        Page<Course> result = courseService.findAll(filter, page, size, sort, asc);

        // Assert
        assert (result.getContent().size() == 2);
        assert (result.getContent().get(0).getName().equals("Cirugia General"));
        assert (result.getContent().get(1).getName().equals("Cirugia Plastica"));
        assert (result.getTotalElements() == 2);
        assert (result.getTotalPages() == 1);
        assert (result.getNumber() == 0);
        assert (result.getSize() == 10);
        assert (result.getSort().equals(Sort.by("courseId").ascending()));
        assert (result.getPageable().getPageSize() == 10);
        assert (!result.hasPrevious());
        assert (!result.hasNext());
    }

    @Test
    public void testCountClasses() {
        // Arrange
        when(classRepository.count()).thenReturn((long) mockClassModels.size());

        // Act
        Long result = courseService.countClasses();

        // Assert
        assert (result == 2);
    }

    @Test
    public void testFindById() {
        // Arrange
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(java.util.Optional.of(mockCourse1));

        // Act
        Course result = courseService.findById(courseId);

        // Assert
        assert (result.getCourseId() == 1L);
        assert (result.getName().equals("Cirugia General"));
    }

    @Test
    public void testSaveCourse() {
        // Arrange
        when(courseRepository.save(mockCourse1)).thenReturn(mockCourse1);
        when(userRepository.findById(mockCourse1.getCoordinator().getId()))
                .thenReturn(java.util.Optional.of(mockCoordinator));

        // Act
        Course result = courseService.save(mockCourseDto);

        // Assert
        assert (result.getName().equals("Cirugia Cardiaca"));
        assert (result.getJaverianaId() == 20003L);
        assert (result.getCoordinator().getId() == 1L);
        assert (result.getFaculty().equals("Medicina"));
        assert (result.getDepartment().equals("Cirugia"));
        assert (result.getProgram().equals("Medicina"));
        assert (result.getSemester() == 7);
    }

    @Test
    public void testSaveCourseInvalidCoordinator() {
        // Arrange
        when(userRepository.findById(mockCourseDto.getCoordinatorId())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            courseService.save(mockCourseDto);
        } catch (Exception e) {
            assert (e instanceof EntityNotFoundException);
            assert (e.getMessage().equals("Coordinator with ID 1 not found"));
        }
    }

    @Test
    public void testDeleteCourseByIdExists() {
        // Arrange
        Long courseId = 1L;

        when(courseRepository.existsById(courseId)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(courseId);

        // Act
        courseService.deleteById(courseId);

        // Assert
        verify(courseRepository, times(1)).existsById(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    public void testDeleteCourseByIdNotExists() {
        // Arrange
        Long courseId = 1L;

        when(courseRepository.existsById(courseId)).thenReturn(false);

        // Act & Assert
        try {
            courseService.deleteById(courseId);
        } catch (Exception e) {
            assert (e instanceof EntityNotFoundException);
            assert (e.getMessage().equals("Course not found with id: " + courseId));
        }

        verify(courseRepository, times(1)).existsById(courseId);
        verify(courseRepository, times(0)).deleteById(courseId);
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    public void testUpdateCourse(){
        // Arrange
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(java.util.Optional.of(mockCourse1));
        when(userRepository.findById(mockCourseDto.getCoordinatorId())).thenReturn(java.util.Optional.of(mockCoordinator));
        when(courseRepository.save(mockCourse1)).thenReturn(mockCourse1);

        // Act
        Course result = courseService.update(mockCourseDto, courseId);

        // Assert
        assert (result.getName().equals("Cirugia Cardiaca"));
        assert (result.getJaverianaId() == 20003L);
        assert (result.getCoordinator().getId() == 1L);
        assert (result.getFaculty().equals("Medicina"));
        assert (result.getDepartment().equals("Cirugia"));
        assert (result.getProgram().equals("Medicina"));
        assert (result.getSemester() == 7);
    }

}
