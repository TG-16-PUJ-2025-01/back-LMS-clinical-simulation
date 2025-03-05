package co.edu.javeriana.lms.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
import co.edu.javeriana.lms.subjects.dtos.ClassDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
import co.edu.javeriana.lms.subjects.services.ClassService;
import jakarta.persistence.EntityNotFoundException;

public class ClassServiceTest {

    @InjectMocks
    private ClassService classService;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private ClassModel mockClass;
    private ClassDto mockClassDto;
    private User mockProfessor;
    private Course mockCourse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockProfessor = new User();
        mockProfessor.setId(1L);
        mockProfessor.setName("John");
        mockProfessor.setLastName("Doe");
        mockProfessor.setRoles(Set.of(Role.PROFESOR));

        mockCourse = new Course();
        mockCourse.setCourseId(1L);
        mockCourse.setName("Mathematics");
        mockCourse.setJaverianaId(101L);

        mockClass = new ClassModel("2023-01", List.of(mockProfessor), mockCourse, 201L);
        mockClass.setClassId(1L);

        mockClassDto = new ClassDto();
        mockClassDto.setPeriod("2023-01");
        mockClassDto.setProfessorsIds(List.of(1L));
        mockClassDto.setCourseId(1L);
        mockClassDto.setJaverianaId(201L);
    }

    @Test
    public void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("period").ascending());
        Page<ClassModel> mockPage = new PageImpl<>(List.of(mockClass), pageable, 1);

        when(classRepository.searchClasses(anyString(), any(Pageable.class))).thenReturn(mockPage);

        Page<ClassModel> result = classService.findAll("2023", 0, 10, "period", true);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockClass, result.getContent().get(0));
    }

    @Test
    public void testFindAllMembers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<User> mockPage = new PageImpl<>(List.of(mockProfessor), pageable, 1);

        when(classRepository.findMembers(anyLong(), anyString(), any(Pageable.class))).thenReturn(mockPage);

        Page<User> result = classService.findAllMembers("John", 0, 10, "name", true, 1L);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockProfessor, result.getContent().get(0));
    }

    @Test
    public void testFindAllNonMembers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<User> mockPage = new PageImpl<>(List.of(mockProfessor), pageable, 1);

        when(classRepository.findUsersNotInClass(anyLong(), anyString(), any(Pageable.class))).thenReturn(mockPage);

        Page<User> result = classService.findAllNonMembers("John", 0, 10, "name", true, 1L);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockProfessor, result.getContent().get(0));
    }

    @Test
    public void testFindById() {
        when(classRepository.findById(1L)).thenReturn(Optional.of(mockClass));

        ClassModel result = classService.findById(1L);

        assertNotNull(result);
        assertEquals(mockClass, result);
    }

    @Test
    public void testFindByIdNotFound() {
        when(classRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classService.findById(1L);
        });
    }

    @Test
    public void testSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockProfessor));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(classRepository.save(any(ClassModel.class))).thenReturn(mockClass);

        ClassModel result = classService.save(mockClassDto);

        assertNotNull(result);
        assertEquals(mockClass.getPeriod(), result.getPeriod());
        assertEquals(mockClass.getProfessors(), result.getProfessors());
        assertEquals(mockClass.getCourse(), result.getCourse());
        assertEquals(mockClass.getJaverianaId(), result.getJaverianaId());
    }

    @Test
    public void testSaveProfessorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classService.save(mockClassDto);
        });
    }

    @Test
    public void testSaveCourseNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockProfessor));
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classService.save(mockClassDto);
        });
    }

    @Test
    public void testDeleteById() {
        doNothing().when(classRepository).deleteById(1L);

        classService.deleteById(1L);

        verify(classRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdate() {
        when(classRepository.findById(1L)).thenReturn(Optional.of(mockClass));
        when(classRepository.save(any(ClassModel.class))).thenReturn(mockClass);

        ClassModel result = classService.update(mockClass);

        assertNotNull(result);
        assertEquals(mockClass, result);
    }

    @Test
    public void testUpdateMembers() {
        // Crear usuarios de prueba
        User newProfessor = new User();
        newProfessor.setId(2L);
        newProfessor.setRoles(Set.of(Role.PROFESOR));

        User newStudent = new User();
        newStudent.setId(3L);
        newStudent.setRoles(Set.of(Role.ESTUDIANTE));

        // Usar una lista mutable en lugar de List.of
        List<User> members = new ArrayList<>();
        members.add(newProfessor);
        members.add(newStudent);

        // Configurar los mocks
        when(classRepository.findById(1L)).thenReturn(Optional.of(mockClass));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newProfessor));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newStudent));
        when(classRepository.save(any(ClassModel.class))).thenReturn(mockClass);

        // Ejecutar el método bajo prueba
        ClassModel result = classService.updateMembers(members, 1L);

        // Verificar los resultados
        assertNotNull(result);
        assertTrue(result.getProfessors().contains(newProfessor));
        assertTrue(result.getStudents().contains(newStudent));
    }

    @Test
    public void testUpdateMembersClassNotFound() {
        when(classRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classService.updateMembers(List.of(), 1L);
        });
    }
}