package co.edu.javeriana.lms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.edu.javeriana.lms.grades.dtos.EvaluatedCriteriaDto;
import co.edu.javeriana.lms.grades.dtos.RubricDto;
import co.edu.javeriana.lms.grades.models.EvaluatedCriteria;
import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import co.edu.javeriana.lms.grades.services.RubricService;
import jakarta.persistence.EntityNotFoundException;

public class RubricServiceTest {

    @Mock
    private RubricRepository rubricRepository;

    @InjectMocks
    private RubricService rubricService;

    private Rubric mockRubric;
    private RubricDto mockRubricDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar objetos de prueba
        mockRubric = Rubric.builder()
                .rubricId(1L)
                .total(new EvaluatedCriteria(UUID.randomUUID(), "Total", 4.5f))
                .build();

        mockRubricDto = RubricDto.builder()
                .evaluatedCriterias(List.of(
                        EvaluatedCriteriaDto.builder()
                                .comment("Buen desempeño")
                                .score(4.0f)
                                .build()))
                .total(EvaluatedCriteriaDto.builder()
                        .comment("Total")
                        .score(4.5f)
                        .build())
                .build();
    }

    @Test
    public void testFindById_Success() {
        // Given
        Long rubricId = 1L;
        when(rubricRepository.findById(rubricId))
                .thenReturn(Optional.of(mockRubric));

        // When
        Rubric result = rubricService.findById(rubricId);

        // Then
        assertNotNull(result);
        assertEquals(rubricId, result.getRubricId());
        assertEquals(4.5f, result.getTotal().getScore(), 0.01);
        verify(rubricRepository, times(1)).findById(rubricId);
    }

    @Test
    public void testFindById_NotFound() {
        // Given
        Long rubricId = 99L;
        when(rubricRepository.findById(rubricId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> {
            rubricService.findById(rubricId);
        });
        verify(rubricRepository, times(1)).findById(rubricId);
    }

    @Test
    public void testCreate_Success() {
        // Given
        when(rubricRepository.save(any(Rubric.class)))
                .thenReturn(mockRubric);

        // When
        Rubric result = rubricService.create(mockRubricDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getRubricId());
        verify(rubricRepository, times(1)).save(any(Rubric.class));
    }

    @Test
    public void testUpdate_Success() {
        // Given
        Long rubricId = 1L;
        when(rubricRepository.findById(rubricId))
                .thenReturn(Optional.of(mockRubric));
        when(rubricRepository.save(any(Rubric.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rubric result = rubricService.update(mockRubricDto, rubricId);

        // Then
        assertNotNull(result);
        assertEquals(rubricId, result.getRubricId());
        verify(rubricRepository, times(1)).findById(rubricId);
        verify(rubricRepository, times(1)).save(any(Rubric.class));
    }

    @Test
    public void testUpdate_NotFound() {
        // Given
        Long rubricId = 99L;
        when(rubricRepository.findById(rubricId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> {
            rubricService.update(mockRubricDto, rubricId);
        });
        verify(rubricRepository, times(1)).findById(rubricId);
        verify(rubricRepository, never()).save(any());
    }
}