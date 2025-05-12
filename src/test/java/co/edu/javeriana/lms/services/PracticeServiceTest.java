package co.edu.javeriana.lms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.PracticeType;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.services.PracticeService;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.grades.repositories.RubricTemplateRepository;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
public class PracticeServiceTest {

    @InjectMocks
    private PracticeService practiceService;

    @Mock
    private PracticeRepository practiceRepository;

    @Mock
    private RubricTemplateRepository rubricTemplateRepository;

    @Mock
    private ClassRepository classRepository;

    private Practice mockPractice;
    private RubricTemplate mockRubricTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar mocks
        mockPractice = Practice.builder()
                .id(1L)
                .name("Práctica 1")
                .description("Descripción de práctica")
                .type(PracticeType.GRUPAL)
                .gradeable(true)
                .simulationDuration(30)
                .numberOfGroups(3)
                .maxStudentsGroup(3)
                .gradePercentage(30f)
                .build();

        mockRubricTemplate = RubricTemplate.builder()
                .rubricTemplateId(1L)
                .title("Rubrica de evaluación")
                .build();
    }

    @Test
    public void testUpdateRubric_Success() {
        // Given
        Long practiceId = 1L;
        Long rubricId = 1L;
        
        when(practiceRepository.findById(practiceId))
                .thenReturn(Optional.of(mockPractice));
        when(rubricTemplateRepository.findById(rubricId))
                .thenReturn(Optional.of(mockRubricTemplate));
        when(practiceRepository.save(any(Practice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Practice result = practiceService.updateRubric(practiceId, rubricId);

        // Then
        assertNotNull(result);
        assertEquals(practiceId, result.getId());
        assertEquals(mockRubricTemplate, result.getRubricTemplate());
        
        verify(practiceRepository, times(1)).findById(practiceId);
        verify(rubricTemplateRepository, times(1)).findById(rubricId);
        verify(practiceRepository, times(1)).save(mockPractice);
    }

    @Test
    public void testUpdateRubric_PracticeNotFound() {
        // Given
        Long practiceId = 99L;
        Long rubricId = 1L;
        
        when(practiceRepository.findById(practiceId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> {
            practiceService.updateRubric(practiceId, rubricId);
        });
        
        verify(practiceRepository, times(1)).findById(practiceId);
        verify(rubricTemplateRepository, never()).findById(any());
        verify(practiceRepository, never()).save(any());
    }

    @Test
    public void testUpdateRubric_RubricNotFound() {
        // Given
        Long practiceId = 1L;
        Long rubricId = 99L;
        
        when(practiceRepository.findById(practiceId))
                .thenReturn(Optional.of(mockPractice));
        when(rubricTemplateRepository.findById(rubricId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> {
            practiceService.updateRubric(practiceId, rubricId);
        });
        
        verify(practiceRepository, times(1)).findById(practiceId);
        verify(rubricTemplateRepository, times(1)).findById(rubricId);
        verify(practiceRepository, never()).save(any());
    }
}