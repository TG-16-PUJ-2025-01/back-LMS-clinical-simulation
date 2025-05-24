package co.edu.javeriana.lms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.grades.dtos.EvaluatedCriteriaDto;
import co.edu.javeriana.lms.grades.dtos.RubricDto;
import co.edu.javeriana.lms.grades.models.*;
import co.edu.javeriana.lms.practices.dtos.SimulationByTimeSlotDto;
import co.edu.javeriana.lms.practices.dtos.TimeSlotDto;
import co.edu.javeriana.lms.practices.models.*;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.practices.services.SimulationService;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class SimulationServiceTest {

    @InjectMocks
    private SimulationService simulationService;

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private PracticeRepository practiceRepository;

    @Mock
    private RubricRepository rubricRepository;

    @Mock
    private RoomRepository roomRepository;

    private static Simulation mockSimulation;
    private static Practice mockPractice;
    private static User mockUser;
    private static Room mockRoom;
    private static RoomType mockRoomType;
    private static Rubric mockRubric;
    private static RubricDto mockRubricDto;
    private static RubricTemplate mockRubricTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setUpAll() {

        // Configuración similar a DBInitializer

        mockRoomType = RoomType.builder().id(1L).name("Cirugia").build();

        mockUser = User.builder()
                .id(1L)
                .email("estudiante@gmail.com")
                .name("Camilo")
                .lastName("Mendoza")
                .institutionalId("00000050001")
                .build();

        mockRoom = Room.builder()
                .id(1L)
                .name("Consultorio 1")
                .capacity(11)
                .ip("10.197.140.234")
                .type(mockRoomType)
                .build();

        mockPractice = Practice.builder()
                .id(1L)
                .name("Práctica 1")
                .description("Descripción de la práctica 1")
                .type(PracticeType.GRUPAL)
                .gradeable(true)
                .numberOfGroups(1)
                .maxStudentsGroup(3)
                .simulationDuration(30)
                .gradePercentage(30f)
                .build();

        mockRubricTemplate = RubricTemplate.builder()
                .rubricTemplateId(1L)
                .title("Rubric Template 1")
                .build();

        mockRubric = Rubric.builder()
                .rubricId(1L)
                .rubricTemplate(mockRubricTemplate)
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

        Date startDate = Date.from(LocalDateTime.now()
                .withHour(9).withMinute(0)
                .atZone(ZoneId.systemDefault()).toInstant());

        Date endDate = Date.from(LocalDateTime.now()
                .withHour(9).withMinute(30)
                .atZone(ZoneId.systemDefault()).toInstant());

        mockSimulation = Simulation.builder()
                .simulationId(1L)
                .groupNumber(1)
                .startDateTime(startDate)
                .endDateTime(endDate)
                .gradeStatus(GradeStatus.PENDING)
                .practice(mockPractice)
                .users(List.of(mockUser))
                .rooms(List.of(mockRoom))
                .rubric(mockRubric)
                .build();
    }

    @Test
    public void testFindAllSimulations() {
        // Given
        List<Simulation> simulations = Arrays.asList(
                mockSimulation,
                Simulation.builder().simulationId(2L).groupNumber(2).build());

        Page<Simulation> mockPage = new PageImpl<>(simulations,
                PageRequest.of(0, 10), simulations.size());

        when(simulationRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(mockPage);

        // When
        Page<Simulation> result = simulationService.findAllSimulations(0, 10);

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getSimulationId());
        assertEquals(2L, result.getContent().get(1).getSimulationId());
        verify(simulationRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    public void testFindSimulationByIdSuccess() {
        // Given
        when(simulationRepository.findById(1L))
                .thenReturn(Optional.of(mockSimulation));

        // When
        Simulation result = simulationService.findSimulationById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getSimulationId());
        assertEquals(1, result.getGroupNumber());
        assertEquals(mockPractice, result.getPractice());
        assertEquals(1, result.getUsers().size());
        assertEquals(mockUser, result.getUsers().get(0));
        assertEquals(1, result.getRooms().size());
        assertEquals(mockRoom, result.getRooms().get(0));
    }

    @Test
    public void testFindSimulationByIdNotFound() {
        // Given
        when(simulationRepository.findById(99L))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> {
            simulationService.findSimulationById(99L);
        });
    }

    @Test
    public void testFindSimulationsByPracticeId() {
        // Given
        List<Simulation> simulations = Arrays.asList(
                mockSimulation,
                Simulation.builder()
                        .simulationId(2L)
                        .groupNumber(2)
                        .practice(mockPractice)
                        .build());

        Page<Simulation> mockPage = new PageImpl<>(simulations,
                PageRequest.of(0, 10, Sort.by("groupNumber").ascending()),
                simulations.size());

        when(practiceRepository.findById(1L))
                .thenReturn(Optional.of(mockPractice));
        when(simulationRepository.findByPracticeId(1L,
                PageRequest.of(0, 10, Sort.by("groupNumber").ascending())))
                .thenReturn(mockPage);

        // When
        Page<Simulation> result = simulationService.findSimulationsByPracticeId(
                1L, 0, 10, "groupNumber", true, null);

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getSimulationId());
        assertEquals(2L, result.getContent().get(1).getSimulationId());
        assertEquals(mockPractice, result.getContent().get(0).getPractice());
    }

    @Test
    public void testUpdateSimulationRubricSuccess() {
        // Given
        when(simulationRepository.findById(1L))
                .thenReturn(Optional.of(mockSimulation));
        when(rubricRepository.save(any(Rubric.class)))
                .thenReturn(mockRubric);

        // When
        Rubric result = simulationService.updateSimulationRubric(1L, mockRubricDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getRubricId());
        assertEquals(mockRubricTemplate, result.getRubricTemplate());
        assertEquals(4.5f, result.getTotal().getScore(), 0.01);
    }

    @Test
    public void testUpdateSimulationRubricWithNewRubric() {
        // 1. Configuración
        Simulation simulationWithoutRubric = Simulation.builder()
                .simulationId(3L)
                .groupNumber(3)
                .practice(mockPractice)
                .build();

        // Configurar mock para findById
        when(simulationRepository.findById(3L))
                .thenReturn(Optional.of(simulationWithoutRubric));

        // Configurar mock para save de Rubric
        when(rubricRepository.save(any(Rubric.class)))
                .thenAnswer(invocation -> {
                    Rubric r = invocation.getArgument(0);
                    r.setRubricId(1L); // Asignar ID mock
                    return r;
                });

        // Configurar mock para save de Simulation
        when(simulationRepository.save(any(Simulation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Ejecución
        Rubric result = simulationService.updateSimulationRubric(3L, mockRubricDto);

        // 3. Verificaciones
        assertNotNull(result);
        assertEquals(1L, result.getRubricId());

        // Verificar que la simulación tiene la rúbrica
        assertNotNull(simulationWithoutRubric.getRubric());
        assertEquals(result, simulationWithoutRubric.getRubric());

        // Verificar relación inversa
        assertEquals(simulationWithoutRubric, result.getSimulation());
    }

    @Test
    public void testPublishGradeSuccess() {
        // Given
        when(simulationRepository.findById(1L))
                .thenReturn(Optional.of(mockSimulation));
        when(simulationRepository.save(mockSimulation))
                .thenReturn(mockSimulation);

        // When
        Simulation result = simulationService.publishGrade(1L);

        // Then
        assertEquals(GradeStatus.REGISTERED, result.getGradeStatus());
        assertEquals(4.5f, result.getGrade(), 0.01);
        assertNotNull(result.getGradeDateTime());
    }

    @Test
    public void testPublishGradeWithoutRubric() {
        // Given
        Simulation simulationWithoutRubric = Simulation.builder()
                .simulationId(3L)
                .groupNumber(3)
                .practice(mockPractice)
                .build();

        when(simulationRepository.findById(3L))
                .thenReturn(Optional.of(simulationWithoutRubric));

        // When / Then
        assertThrows(IllegalStateException.class, () -> {
            simulationService.publishGrade(3L);
        });
    }

    @Test
    public void testFindSimulationsByPracticeIdAndGroupNumber() {
        // Given
        Simulation expectedSimulation = Simulation.builder()
                .simulationId(1L)
                .groupNumber(1)
                .practice(mockPractice)
                .build();

        List<Simulation> simulations = Collections.singletonList(expectedSimulation);
        Page<Simulation> mockPage = new PageImpl<>(simulations, PageRequest.of(0, 10), simulations.size());

        when(practiceRepository.findById(1L))
                .thenReturn(Optional.of(mockPractice));
        when(simulationRepository.findByPracticeIdAndGroupNumber(1L, 1, PageRequest.of(0, 10)))
                .thenReturn(mockPage);

        // When - Llamamos sin parámetros sort/asc
        Page<Simulation> result = simulationService.findSimulationsByPracticeId(
                1L, 0, 10, null, null, 1);

        // Then
        assertEquals(1, result.getTotalElements());
        Simulation actualSimulation = result.getContent().get(0);
        assertEquals(1L, actualSimulation.getSimulationId());
        assertEquals(1, actualSimulation.getGroupNumber());
        assertEquals(mockPractice, actualSimulation.getPractice());

        verify(practiceRepository, times(1)).findById(1L);
        verify(simulationRepository, times(1))
                .findByPracticeIdAndGroupNumber(1L, 1, PageRequest.of(0, 10));
    }

    @Test
    public void testAddSimulations() {
        // Given
        when(practiceRepository.findById(1L))
                .thenReturn(Optional.of(mockPractice));
        when(simulationRepository.save(any(Simulation.class)))
                .thenReturn(mockSimulation);
        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(mockRoom));
        when(simulationRepository.isRoomAvailable(eq(mockRoom), any(Date.class), any(Date.class)))
                .thenReturn(true);

        // When
        List<Simulation> result = simulationService.addSimulations(List.of(SimulationByTimeSlotDto.builder()
                .startDateTime(mockSimulation.getStartDateTime())
                .endDateTime(mockSimulation.getEndDateTime())
                .practiceId(1L)
                .roomIds(List.of(1L))
                .build()));

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getGroupNumber());
    }

    @Test
    public void testUpdateSimulation() {
        // Given
        when(simulationRepository.findById(1L))
                .thenReturn(Optional.of(mockSimulation));
        when(simulationRepository.save(any(Simulation.class)))
                .thenReturn(mockSimulation);
        when(simulationRepository.isRoomAvailable(eq(mockRoom), any(Date.class), any(Date.class)))
                .thenReturn(true);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        // When
        Simulation result = simulationService.updateSimulation(1L, SimulationByTimeSlotDto.builder()
                .startDateTime(mockSimulation.getStartDateTime())
                .endDateTime(mockSimulation.getEndDateTime())
                .practiceId(1L)
                .roomIds(List.of(1L))
                .build());

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getSimulationId());
        assertEquals(mockPractice, result.getPractice());
    }

    @Test
    public void testDeleteSimulationById() {
        // Given
        when(simulationRepository.existsById(1L))
                .thenReturn(true);

        // When
        simulationService.deleteSimulationById(1L);

        // Then
        verify(simulationRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteSimulationByIdNotFound() {
        // Given
        when(simulationRepository.existsById(99L))
                .thenReturn(false);

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> {
            simulationService.deleteSimulationById(99L);
        });
    }

    @Test
    public void testFindSimulationsSchedule() {
        String mockDate = "2023-10-01T09:00:00Z";
        // Given
        List<Simulation> simulations = Arrays.asList(
                mockSimulation,
                Simulation.builder()
                        .simulationId(2L)
                        .groupNumber(2)
                        .practice(mockPractice)
                        .rooms(List.of(mockRoom))
                        .startDateTime(Date.from(Instant.parse(mockDate)))
                        .endDateTime(Date.from(Instant.parse(mockDate).plusSeconds(3600)))
                        .build());

        when(simulationRepository.findByStartDateTimeBetween(
                any(Date.class), any(Date.class)))
                .thenReturn(simulations);

        // When
        List<TimeSlotDto> result = simulationService.findSimulationsSchedule(mockDate);

        // Then
        assertEquals(2, result.size());
        assertEquals(mockRoom.getName(), result.get(0).getRoom());
        assertEquals(mockRoom.getName(), result.get(1).getRoom());
    }
}