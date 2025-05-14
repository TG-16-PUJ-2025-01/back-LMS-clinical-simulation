package co.edu.javeriana.lms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.grades.dtos.RubricTemplateDTO;
import co.edu.javeriana.lms.grades.models.*;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import co.edu.javeriana.lms.grades.repositories.RubricTemplateRepository;
import co.edu.javeriana.lms.grades.services.RubricTemplateService;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.PracticeType;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class RubricTemplateServiceTest {

    @Mock
    private RubricTemplateRepository rubricTemplateRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PracticeRepository practiceRepository;

    @Mock
    private RubricRepository rubricRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RubricTemplateService rubricTemplateService;

    private RubricTemplate mockRubricTemplate;
    private RubricTemplateDTO mockRubricTemplateDTO;
    private User mockUser;
    private Course mockCourse;
    private Practice mockPractice;
    private Criteria mockCriteria;
    private RubricColumn mockColumn;

    @BeforeEach
    public void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("profesor@javeriana.edu.co")
                .roles(Set.of(Role.PROFESOR))
                .build();

        mockCourse = new Course();
        mockCourse.setCourseId(1L);

        mockPractice = Practice.builder()
                .id(1L)
                .name("Práctica 1")
                .description("Descripción de práctica")
                .type(PracticeType.GRUPAL)
                .gradeable(true)
                .simulations(new ArrayList<>())
                .build();

        mockCriteria = new Criteria();
        mockCriteria.setId(UUID.randomUUID());
        mockCriteria.setName("Criterio 1");

        mockColumn = new RubricColumn();
        mockColumn.setTitle("Nivel 1");

        mockRubricTemplate = RubricTemplate.builder()
                .rubricTemplateId(1L)
                .title("Rúbrica de evaluación")
                .criteria(List.of(mockCriteria))
                .columns(List.of(mockColumn))
                .creationDate(new Date())
                .archived(false)
                .courses(new ArrayList<>(List.of(mockCourse)))
                .practices(new ArrayList<>())
                .rubrics(new ArrayList<>())
                .creator(mockUser)
                .build();

        mockRubricTemplateDTO = RubricTemplateDTO.builder()
                .title("Rúbrica de evaluación")
                .criteria(List.of(mockCriteria))
                .columns(List.of(mockColumn))
                .courses(List.of(mockCourse.getCourseId()))
                .practiceId(mockPractice.getId())
                .archived(false)
                .build();
    }

    @Test
    public void testFindAll_AdminUser() {
        // Configurar usuario admin
        User adminUser = User.builder()
                .email("admin@javeriana.edu.co")
                .roles(Set.of(Role.ADMIN))
                .build();

        when(userRepository.findByEmail("admin@javeriana.edu.co")).thenReturn(Optional.of(adminUser));
        when(rubricTemplateRepository.findArchivedByTitleOrCreationDateContaining(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockRubricTemplate)));

        Page<RubricTemplate> result = rubricTemplateService.findAll("filter", 0, 10, "title", true, true,
                "admin@javeriana.edu.co");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(rubricTemplateRepository).findArchivedByTitleOrCreationDateContaining(anyString(), any(Pageable.class));
    }

    @Test
    public void testFindAll_ProfessorUser() {
        when(userRepository.findByEmail("profesor@javeriana.edu.co")).thenReturn(Optional.of(mockUser));
        when(rubricTemplateRepository.findArchivedMineByTitleOrCreationDateContaining(anyString(), anyLong(),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockRubricTemplate)));

        Page<RubricTemplate> result = rubricTemplateService.findAll("filter", 0, 10, "title", true, true,
                "profesor@javeriana.edu.co");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(rubricTemplateRepository).findArchivedMineByTitleOrCreationDateContaining(anyString(), anyLong(),
                any(Pageable.class));
    }

    @Test
    public void testFindAll_NotArchived() {
        when(userRepository.findByEmail("profesor@javeriana.edu.co")).thenReturn(Optional.of(mockUser));
        when(rubricTemplateRepository.findNotArchivedByTitleOrCreationDateContaining(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockRubricTemplate)));

        Page<RubricTemplate> result = rubricTemplateService.findAll("filter", 0, 10, "title", true, false,
                "profesor@javeriana.edu.co");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(rubricTemplateRepository).findNotArchivedByTitleOrCreationDateContaining(anyString(),
                any(Pageable.class));
    }

    @Test
    public void testFindById_Success() {
        when(rubricTemplateRepository.findById(1L)).thenReturn(Optional.of(mockRubricTemplate));

        RubricTemplate result = rubricTemplateService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getRubricTemplateId());
    }

    @Test
    public void testFindById_NotFound() {
        when(rubricTemplateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            rubricTemplateService.findById(1L);
        });
    }

    @Test
    public void testArchiveById() {
        when(rubricTemplateRepository.findById(1L)).thenReturn(Optional.of(mockRubricTemplate));
        when(rubricTemplateRepository.save(any(RubricTemplate.class))).thenReturn(mockRubricTemplate);

        RubricTemplate result = rubricTemplateService.archiveById(1L);

        assertTrue(result.getArchived());
    }

    @Test
    public void testUnarchiveById() {
        mockRubricTemplate.setArchived(true);
        when(rubricTemplateRepository.findById(1L)).thenReturn(Optional.of(mockRubricTemplate));
        when(rubricTemplateRepository.save(any(RubricTemplate.class))).thenReturn(mockRubricTemplate);

        RubricTemplate result = rubricTemplateService.unarchiveById(1L);

        assertFalse(result.getArchived());
    }

    @Test
    public void testSave_WithPractice() {
        // Configurar mocks
        when(userRepository.findByEmail("profesor@javeriana.edu.co")).thenReturn(Optional.of(mockUser));
        when(practiceRepository.findById(1L)).thenReturn(Optional.of(mockPractice));
        when(courseRepository.findAllById(anyList())).thenReturn(List.of(mockCourse));

        // Configurar el mock para save
        when(rubricTemplateRepository.save(any(RubricTemplate.class))).thenAnswer(invocation -> {
            RubricTemplate rt = invocation.getArgument(0);
            rt.setRubricTemplateId(1L); // Asignar ID simulado
            rt.setPractices(new ArrayList<>(List.of(mockPractice))); // Asegurar lista de prácticas
            return rt;
        });

        // Ejecutar
        RubricTemplate result = rubricTemplateService.save(mockRubricTemplateDTO, "profesor@javeriana.edu.co");

        // Verificar
        assertNotNull(result);
        assertEquals(1, result.getPractices().size());
        assertEquals(mockPractice, result.getPractices().get(0));
        verify(practiceRepository).findById(1L);
    }

    @Test
    public void testSave_WithoutPractice() {
        mockRubricTemplateDTO.setPracticeId(null);
        when(userRepository.findByEmail("profesor@javeriana.edu.co")).thenReturn(Optional.of(mockUser));
        when(courseRepository.findAllById(anyList())).thenReturn(List.of(mockCourse));
        when(rubricTemplateRepository.save(any(RubricTemplate.class))).thenReturn(mockRubricTemplate);

        RubricTemplate result = rubricTemplateService.save(mockRubricTemplateDTO, "profesor@javeriana.edu.co");

        assertNotNull(result);
        assertEquals(0, result.getPractices().size());
        verify(practiceRepository, never()).findById(anyLong());
    }

    @Test
    public void testDeleteById_Success() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -4); // Hace 4 años
        mockRubricTemplate.setCreationDate(cal.getTime());
        mockRubricTemplate.setRubrics(new ArrayList<>());

        when(rubricTemplateRepository.findById(1L)).thenReturn(Optional.of(mockRubricTemplate));
        doNothing().when(rubricTemplateRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            rubricTemplateService.deleteById(1L);
        });

        verify(rubricTemplateRepository).deleteById(1L);
    }

    @Test
    public void testDeleteById_WithRecentRubrics() {
        Rubric rubric = new Rubric();
        mockRubricTemplate.setRubrics(List.of(rubric));

        when(rubricTemplateRepository.findById(1L)).thenReturn(Optional.of(mockRubricTemplate));

        assertThrows(IllegalStateException.class, () -> {
            rubricTemplateService.deleteById(1L);
        });

        verify(rubricTemplateRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testUpdate() {
        when(rubricTemplateRepository.findById(1L)).thenReturn(Optional.of(mockRubricTemplate));
        when(courseRepository.findAllById(anyList())).thenReturn(List.of(mockCourse));
        when(rubricTemplateRepository.save(any(RubricTemplate.class))).thenReturn(mockRubricTemplate);

        RubricTemplate result = rubricTemplateService.update(mockRubricTemplateDTO, 1L);

        assertNotNull(result);
        verify(rubricTemplateRepository).save(any(RubricTemplate.class));
    }

    @Test
    public void testFindRecommendedRubricTemplatesByCoursesById() {
        when(rubricTemplateRepository.findRecommendedRubricTemplatesByCoursesById(1L))
                .thenReturn(List.of(mockRubricTemplate));

        List<RubricTemplate> result = rubricTemplateService.findRecommendedRubricTemplatesByCoursesById(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}