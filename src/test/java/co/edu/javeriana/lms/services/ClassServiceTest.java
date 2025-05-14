package co.edu.javeriana.lms.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import co.edu.javeriana.lms.shared.errors.CustomError;
import co.edu.javeriana.lms.subjects.dtos.ClassDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
import co.edu.javeriana.lms.subjects.services.ClassService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
public class ClassServiceTest {

    @InjectMocks
    private ClassService classService;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private static User mockCoordinator;
    private static User mockProfessor;
    private static User mockStudent1;
    private static User mockStudent2;
    private static Course mockCourse;
    private static RubricTemplate mockRubricTemplate;
    private static ClassModel mockClass1;
    private static ClassModel mockClass2;
    private static Page<ClassModel> mockClassesPage;
    private static Page<User> mockUsersPage;
    private static ClassDto mockClassDto;
    private static List<ClassModel> mockClassModels;

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

        // Mock Professor
        mockProfessor = User.builder()
                .id(2L)
                .email("john.smith@example.com")
                .password("password123")
                .roles(new HashSet<>(Set.of(Role.PROFESOR)))
                .preferredRole(Role.PROFESOR)
                .name("John")
                .lastName("Smith")
                .institutionalId("00032314")
                .build();

        // Mock Students
        mockStudent1 = User.builder()
                .id(3L)
                .email("maria.nieto@example.com")
                .password("password123")
                .roles(new HashSet<>(Set.of(Role.ESTUDIANTE)))
                .preferredRole(Role.ESTUDIANTE)
                .name("Maria")
                .lastName("Nieto")
                .institutionalId("00032315")
                .build();

        mockStudent2 = User.builder()
                .id(4L)
                .email("carlos.gonzalez@example.com")
                .password("password123")
                .roles(new HashSet<>(Set.of(Role.ESTUDIANTE)))
                .preferredRole(Role.ESTUDIANTE)
                .name("Carlos")
                .lastName("Gonzalez")
                .institutionalId("00032316")
                .build();

        // Mock Course
        mockCourse = Course.builder()
                .courseId(1L)
                .javerianaId(20001L)
                .name("Cirugia General")
                .faculty("Medicina")
                .department("Cirugia")
                .program("Medicina")
                .semester(5)
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

        // Mock ClassDto
        mockClassDto = ClassDto.builder()
                .javerianaId(20001L)
                .professorsIds(List.of(2L))
                .courseId(1L)
                .period("2025-10")
                .numberOfParticipants(20)
                .build();

        // Set relationships
        mockClass1.setCourse(mockCourse);
        mockClass1.setProfessors(new ArrayList<>(List.of(mockProfessor)));
        mockClass1.setStudents(new ArrayList<>(List.of(mockStudent1, mockStudent2)));
        mockClass1.setPractices(new ArrayList<>());

        mockClass2.setCourse(mockCourse);
        mockClass2.setProfessors(new ArrayList<>(List.of(mockProfessor)));
        mockClass2.setStudents(new ArrayList<>(List.of(mockStudent1, mockStudent2)));
        mockClass2.setPractices(new ArrayList<>());

        mockCourse.setClassModels(List.of(mockClass1, mockClass2));
        mockCourse.setCoordinator(mockCoordinator);
        mockCourse.setRubricTemplates(List.of(mockRubricTemplate));

        mockClassModels = Arrays.asList(mockClass1, mockClass2);

        mockClassesPage = new PageImpl<>(Arrays.asList(mockClass1, mockClass2),
                PageRequest.of(0, 10, Sort.by("classId").ascending()), mockClassModels.size());

        List<User> mockUsers = List.of(mockStudent1, mockStudent2);
        mockUsersPage = new PageImpl<>(mockUsers, PageRequest.of(0, 10, Sort.by("name").ascending()), mockUsers.size());
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Reset the mock classes to avoid side effects between tests
        mockClass1.setProfessors(new ArrayList<>(List.of(mockProfessor)));
        mockClass1.setStudents(new ArrayList<>(List.of(mockStudent1, mockStudent2)));
        mockClass1.setPractices(new ArrayList<>());

        mockClass2.setProfessors(new ArrayList<>(List.of(mockProfessor)));
        mockClass2.setStudents(new ArrayList<>(List.of(mockStudent1, mockStudent2)));
        mockClass2.setPractices(new ArrayList<>());
    }

    @Test
    public void testSearchClasses() {
        // Arrange
        String filter = "";
        int page = 0;
        int size = 10;
        String sort = "classId";
        boolean asc = true;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        // Mock the repository behavior
        when(classRepository.searchClasses(filter, pageable)).thenReturn(mockClassesPage);

        // Act
        Page<ClassModel> result = classService.findAll(filter, page, size, sort, asc);

        // Assert
        assert (result != null);
        assert (result.getTotalElements() == 2);
        assert (result.getContent().size() == 2);
        assert (result.getContent().get(0).getClassId().equals(mockClass1.getClassId()));
        assert (result.getContent().get(1).getClassId().equals(mockClass2.getClassId()));
        assert (result.getNumber() == 0);
        assert (result.getSize() == 10);
        assert (result.getTotalPages() == 1);
        assert (result.getSort().equals(mockClassesPage.getSort()));
        assert (!result.hasPrevious());
        assert (!result.hasNext());
    }

    @Test
    public void testFindAllStudentMembers() {
        // Mock the repository behavior
        when(classRepository.findStudentsMembers(1L, "", mockUsersPage.getPageable()))
                .thenReturn(mockUsersPage);

        // Act
        Page<User> result = classService.findAllMembers("", 0, 10, "name", true, 1L, Role.ESTUDIANTE.name());

        // Assert
        assert (result.getTotalElements() == 2);
        assert (result.getContent().size() == 2);
        assert (result.getContent().get(0).getId().equals(mockStudent1.getId()));
        assert (result.getContent().get(1).getId().equals(mockStudent2.getId()));
        assert (result.getNumber() == 0);
        assert (result.getSize() == 10);
        assert (result.getTotalPages() == 1);
        assert (result.getSort().equals(mockUsersPage.getSort()));
        assert (!result.hasPrevious());
        assert (!result.hasNext());
    }

    @Test
    public void testFindAllProfessorsMembers() {
        // Mock the repository behavior
        when(classRepository.findProfessorsMembers(1L, "", mockUsersPage.getPageable()))
                .thenReturn(mockUsersPage);

        // Act
        Page<User> result = classService.findAllMembers("", 0, 10, "name", true, 1L, Role.PROFESOR.name());

        // Assert
        assert (result.getTotalElements() == 2);
        assert (result.getContent().size() == 2);
        assert (result.getContent().get(0).getId().equals(mockStudent1.getId()));
        assert (result.getContent().get(1).getId().equals(mockStudent2.getId()));
        assert (result.getNumber() == 0);
        assert (result.getSize() == 10);
        assert (result.getTotalPages() == 1);
        assert (result.getSort().equals(mockUsersPage.getSort()));
        assert (!result.hasPrevious());
        assert (!result.hasNext());
    }

    @Test
    public void testFindAllProfesorNonMembers() {
        // Mock the repository behavior
        when(classRepository.findProfessorsNotInClass(1L, "", mockUsersPage.getPageable()))
                .thenReturn(mockUsersPage);

        // Act
        Page<User> result = classService.findAllNonMembers("", 0, 10, "name", true, 1L, Role.PROFESOR.name());

        // Assert
        assert (result.getTotalElements() == 2);
        assert (result.getContent().size() == 2);
        assert (result.getContent().get(0).getId().equals(mockStudent1.getId()));
        assert (result.getContent().get(1).getId().equals(mockStudent2.getId()));
        assert (result.getNumber() == 0);
        assert (result.getSize() == 10);
        assert (result.getTotalPages() == 1);
        assert (result.getSort().equals(mockUsersPage.getSort()));
        assert (!result.hasPrevious());
        assert (!result.hasNext());
    }

    @Test
    public void testFindAllStudentNonMembers() {
        // Mock the repository behavior
        when(classRepository.findStudentsNotInClass(1L, "", mockUsersPage.getPageable()))
                .thenReturn(mockUsersPage);

        // Act
        Page<User> result = classService.findAllNonMembers("", 0, 10, "name", true, 1L, Role.ESTUDIANTE.name());

        // Assert
        assert (result.getTotalElements() == 2);
        assert (result.getContent().size() == 2);
        assert (result.getContent().get(0).getId().equals(mockStudent1.getId()));
        assert (result.getContent().get(1).getId().equals(mockStudent2.getId()));
        assert (result.getNumber() == 0);
        assert (result.getSize() == 10);
        assert (result.getTotalPages() == 1);
        assert (result.getSort().equals(mockUsersPage.getSort()));
        assert (!result.hasPrevious());
        assert (!result.hasNext());
    }

    @Test
    public void testFindClassById() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.of(mockClass1));

        // Act
        ClassModel result = classService.findById(1L);

        // Assert
        assert (result != null);
        assert (result.getClassId().equals(mockClass1.getClassId()));
        assert (result.getJaverianaId().equals(mockClass1.getJaverianaId()));
        assert (result.getPeriod().equals(mockClass1.getPeriod()));
        assert (result.getNumberOfParticipants().equals(mockClass1.getNumberOfParticipants()));
        assert (result.getCourse().getCourseId().equals(mockClass1.getCourse().getCourseId()));
        assert (result.getProfessors().size() == 1);
        assert (result.getProfessors().get(0).getId().equals(mockProfessor.getId()));
        assert (result.getStudents().size() == 2);
    }

    @Test
    public void testFindClassByIdNotFound() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            classService.findById(1L);
        } catch (Exception e) {
            assert (e.getMessage().equals("Class with ID 1 not found"));
        }

        // Verify that the repository method was called
        verify(classRepository, times(1)).findById(1L);
    }

    @Test
    public void testSaveClass() {
        // Mock the repository behavior
        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(mockCourse));
        when(classRepository.save(mockClass1)).thenReturn(mockClass1);
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(mockProfessor));

        // Act
        ClassModel result = classService.save(mockClassDto);

        // Assert
        assert (result != null);
        assert (result.getJaverianaId().equals(mockClass1.getJaverianaId()));
        assert (result.getPeriod().equals(mockClass1.getPeriod()));
        assert (result.getNumberOfParticipants().equals(mockClass1.getNumberOfParticipants()));
        assert (result.getCourse().getCourseId().equals(mockClass1.getCourse().getCourseId()));
        assert (result.getProfessors().size() == 1);
        assert (result.getProfessors().get(0).getId().equals(mockProfessor.getId()));
        assert (result.getNumberOfParticipants() == 20);
    }

    @Test
    public void testSaveClassProfesorNotFound() {
        // Mock the repository behavior
        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(mockCourse));
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            classService.save(mockClassDto);
        } catch (Exception e) {
            assert (e.getMessage().equals("No se encontró el profesor con ID o institutionalId: 2"));
        }

        // Verify that the repository method was called
        verify(classRepository, times(0)).save(mockClass1);
        verify(userRepository, times(1)).findById(2L);
        verify(courseRepository, times(0)).findById(1L);
    }

    @Test
    public void testSaveClassByExcel() {
        // Arrange
        when(userRepository.findByInstitutionalId(2L)).thenReturn(java.util.Optional.of(mockProfessor));
        when(courseRepository.findByJaverianaId(1L)).thenReturn(java.util.Optional.of(mockCourse));
        when(classRepository.save(any(ClassModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ClassModel result = classService.saveByExcel(mockClassDto);

        // Assert
        assert (result != null);
        assert (result.getJaverianaId().equals(20001L));
        assert (result.getPeriod().equals("2025-10"));
        assert (result.getNumberOfParticipants().equals(20));
        assert (result.getProfessors().size() == 1);
        assert (result.getProfessors().get(0).getId().equals(2L));
        assert (result.getCourse().getCourseId().equals(1L));
    }

    @Test
    public void testDeleteById() {
        // Mock the repository behavior
        doNothing().when(classRepository).deleteById(1L);

        // Act
        classService.deleteById(1L);

        // Assert
        verify(classRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateClass() {
        // Mock the repository behavior
        when(classRepository.save(mockClass1)).thenReturn(mockClass1);

        // Act
        classService.update(mockClass1);

        // Assert
        verify(classRepository, times(1)).save(mockClass1);
    }

    @Test
    public void testUpdateMembers() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.of(mockClass1));
        when(userRepository.findById(3L)).thenReturn(java.util.Optional.of(mockStudent1));
        when(userRepository.findById(4L)).thenReturn(java.util.Optional.of(mockStudent2));
        when(classRepository.save(mockClass1)).thenReturn(mockClass1);

        // Act
        classService.updateMembers(List.of(mockStudent1, mockStudent2), 1L);

        // Assert
        assert (mockClass1.getStudents().size() == 2);
        assert (mockClass1.getStudents().get(0).getId().equals(mockStudent1.getId()));
        assert (mockClass1.getStudents().get(1).getId().equals(mockStudent2.getId()));
        assert (mockClass1.getStudents().get(0).getInstitutionalId().equals(mockStudent1.getInstitutionalId()));
        assert (mockClass1.getStudents().get(1).getInstitutionalId().equals(mockStudent2.getInstitutionalId()));

        // Verify interactions
        verify(classRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(3L);
        verify(userRepository, times(1)).findById(4L);
        verify(classRepository, times(1)).save(mockClass1);
    }

    @Test
    public void testUpdateMembersClassNotFound() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            classService.updateMembers(List.of(mockStudent1, mockStudent2), 1L);
        } catch (Exception e) {
            assert (e.getMessage().equals("Class with ID 1 not found"));
        }

        // Verify interactions
        verify(classRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).findById(3L);
        verify(userRepository, times(0)).findById(4L);
    }

    @Test
    public void testUpdateMemberEstudiante() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.of(mockClass1));
        when(userRepository.findByInstitutionalId(3L)).thenReturn(java.util.Optional.of(mockStudent1));
        when(userRepository.findById(3L)).thenReturn(java.util.Optional.of(mockStudent1));
        when(classRepository.save(mockClass1)).thenReturn(mockClass1);

        // Act
        ClassModel classModel = classService.updateMember(1L, 3L, Role.ESTUDIANTE);

        // Assert
        assert (classModel != null);
        assert (classModel.getStudents().size() == 2);
        assert (classModel.getStudents().contains(mockStudent1));
        assert (!classModel.getProfessors().contains(mockStudent1)); // Ensure the user is not in the professors list
    }

    @Test
    public void testUpdateMemberProfesor() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.of(mockClass1));
        when(userRepository.findByInstitutionalId(2L)).thenReturn(java.util.Optional.of(mockProfessor));
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(mockProfessor));
        when(classRepository.save(mockClass1)).thenReturn(mockClass1);

        // Act
        ClassModel classModel = classService.updateMember(1L, 2L, Role.PROFESOR);

        // Assert
        assert (classModel != null);
        assert (classModel.getProfessors().size() == 1);
        assert (classModel.getProfessors().contains(mockProfessor));
        assert (!classModel.getStudents().contains(mockProfessor)); // Ensure the user is not in the students list
    }

    @Test
    public void testUpdateMemberClassNotFound() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            classService.updateMember(1L, 3L, Role.ESTUDIANTE);
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Class with ID 1 not found"));
        }

        // Verify interactions
        verify(classRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).findByInstitutionalId(3L);
    }

    @Test
    public void testUpdateMemberUserNotFound() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.of(mockClass1));
        when(userRepository.findByInstitutionalId(3L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        try {
            classService.updateMember(1L, 3L, Role.ESTUDIANTE);
        } catch (CustomError e) {
            assert (e.getMessage().equals("Usuario con ID 3 no encontrado"));
        }

        // Verify interactions
        verify(classRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByInstitutionalId(3L);
    }

    @Test
    public void testUpdateMemberUserWithoutRole() {
        // Mock the repository behavior
        User invalidUser = User.builder()
                .id(5L)
                .email("invalid.user@example.com")
                .roles(new HashSet<>()) // No roles assigned
                .build();

        when(classRepository.findById(1L)).thenReturn(java.util.Optional.of(mockClass1));
        when(userRepository.findByInstitutionalId(5L)).thenReturn(java.util.Optional.of(invalidUser));

        // Act & Assert
        try {
            classService.updateMember(1L, 5L, Role.ESTUDIANTE);
        } catch (CustomError e) {
            assert (e.getMessage().equals("El usuario no tiene rol estudiante"));
        }

        // Verify interactions
        verify(classRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByInstitutionalId(5L);
    }

    @Test
    public void testUpdateMemberUserAlreadyInList() {
        // Mock the repository behavior
        when(classRepository.findById(1L)).thenReturn(java.util.Optional.of(mockClass1));
        when(userRepository.findByInstitutionalId(3L)).thenReturn(java.util.Optional.of(mockStudent1));

        // Act
        ClassModel classModel = classService.updateMember(1L, 3L, Role.ESTUDIANTE);

        // Assert
        assert (classModel != null);
        assert (classModel.getStudents().size() == 2); // No changes made
        assert (classModel.getStudents().contains(mockStudent1));

        // Verify interactions
        verify(classRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByInstitutionalId(3L);
        verify(classRepository, times(0)).save(mockClass1); // No save operation should occur
    }

    @Test
    public void testFindClassesByProfessorIdAndFilters() {
        // Mock the repository behavior
        when(classRepository.findByProfessors_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(2L, "",
                "2025-10"))
                .thenReturn(mockClassModels);

        // Act
        List<ClassModel> result = classService.findByProfessorIdAndFilters(2L, Integer.valueOf(2025),
                Integer.valueOf(10), "");

        // Assert
        assert (result != null);
        assert (result.size() == 2);
        assert (result.get(0).getClassId().equals(mockClass2.getClassId()));
        assert (result.get(1).getClassId().equals(mockClass1.getClassId()));
        assert (result.get(0).getProfessors().get(0).getId().equals(mockProfessor.getId()));
        assert (result.get(1).getProfessors().get(0).getId().equals(mockProfessor.getId()));
    }

    @Test
    public void testFindClassesByProfessorIdAndFiltersWithYearOnly() {
        // Mock the repository behavior
        when(classRepository.findByProfessors_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(2L, "", "2025"))
                .thenReturn(mockClassModels);

        // Act
        List<ClassModel> result = classService.findByProfessorIdAndFilters(2L, Integer.valueOf(2025), null, "");

        // Assert
        assert (result != null);
        assert (result.size() == 2);
        assert (result.get(0).getClassId().equals(mockClass2.getClassId()));
        assert (result.get(1).getClassId().equals(mockClass1.getClassId()));
    }

    @Test
    public void testFindClassesByProfessorIdAndFiltersWithPeriodOnly() {
        // Mock the repository behavior
        when(classRepository.findByProfessors_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(2L, "", "-10"))
                .thenReturn(mockClassModels);

        // Act
        List<ClassModel> result = classService.findByProfessorIdAndFilters(2L, null, Integer.valueOf(10), "");

        // Assert
        assert (result != null);
        assert (result.size() == 2);
    }

    @Test
    public void testFindClassesByProfessorIdAndFiltersWithNoFilters() {
        // Mock the repository behavior
        when(classRepository.findByProfessors_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(2L, "", ""))
                .thenReturn(mockClassModels);

        // Act
        List<ClassModel> result = classService.findByProfessorIdAndFilters(2L, null, null, "");

        // Assert
        assert (result != null);
        assert (result.size() == 2);
        assert (result.get(0).getClassId().equals(mockClass2.getClassId()));
        assert (result.get(1).getClassId().equals(mockClass1.getClassId()));
    }

    @Test
    public void testFindClassesByProfessorIdAndFiltersWithFilter() {
        // Mock the repository behavior
        when(classRepository.findByProfessors_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(2L, "Cirugia",
                "2025-10"))
                .thenReturn(Arrays.asList(mockClass1));

        // Act
        List<ClassModel> result = classService.findByProfessorIdAndFilters(2L, Integer.valueOf(2025),
                Integer.valueOf(10), "Cirugia");

        // Assert
        assert (result != null);
        assert (result.size() == 1);
        assert (result.get(0).getClassId().equals(mockClass1.getClassId()));
    }

    @Test
    public void testFindClassesByStudentIdAndFilters() {
        // Mock the repository behavior
        when(classRepository.findByStudents_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(3L, "", "2025-10"))
                .thenReturn(mockClassModels);

        // Act
        List<ClassModel> result = classService.findByStudentIdAndFilters(3L, Integer.valueOf(2025),
                Integer.valueOf(10), "");

        // Assert
        assert (result != null);
        assert (result.size() == 2);
        assert (result.get(0).getClassId().equals(mockClass2.getClassId()));
        assert (result.get(1).getClassId().equals(mockClass1.getClassId()));
    }

    @Test
    public void testFindClassesByStudentIdAndFiltersWithYearOnly() {
        // Mock the repository behavior
        when(classRepository.findByStudents_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(3L, "", "2025"))
                .thenReturn(mockClassModels);

        // Act
        List<ClassModel> result = classService.findByStudentIdAndFilters(3L, Integer.valueOf(2025), null, "");

        // Assert
        assert (result != null);
        assert (result.size() == 2);
        assert (result.get(0).getClassId().equals(mockClass2.getClassId()));
        assert (result.get(1).getClassId().equals(mockClass1.getClassId()));
    }

    @Test
    public void testFindClassesByStudentIdAndFiltersWithPeriodOnly() {
        // Mock the repository behavior
        when(classRepository.findByStudents_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(3L, "", "-10"))
                .thenReturn(Arrays.asList(mockClass1));

        // Act
        List<ClassModel> result = classService.findByStudentIdAndFilters(3L, null, Integer.valueOf(10), "");

        // Assert
        assert (result != null);
        assert (result.size() == 1);
    }

    @Test
    public void testFindClassesByStudentIdAndFiltersWithNoFilters() {
        // Mock the repository behavior
        when(classRepository.findByStudents_IdAndCourse_NameContainingIgnoreCaseAndPeriodContaining(3L, "", ""))
                .thenReturn(Arrays.asList(mockClass1));

        // Act
        List<ClassModel> result = classService.findByStudentIdAndFilters(3L, null, null, "");

        // Assert
        assert (result != null);
        assert (result.size() == 1);
        assert (result.get(0).getClassId().equals(mockClass1.getClassId()));
    }
}
