package co.edu.javeriana.lms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.booking.dtos.EventDto;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.services.CalendarService;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.practices.models.PracticeType;

@SpringBootTest
@ActiveProfiles("test")
public class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;

    @Mock
    private ClassRepository classModelRepository;

    @Mock
    private PracticeRepository practiceRepository;

    @Mock
    private SimulationRepository simulationRepository;

    private static Simulation simulation;
    private static Practice practice;
    private static ClassModel classModel;
    private static RoomType mockRoomType;
    private static Room mockRoom1;
    private static Room mockRoom2;
    private static Course mockCourse1;
    private static List<Simulation> simulations;
    private static List<Room> rooms;

    private static final String START = "2024-06-01 08:00";
    private static final String END = "2024-06-01 10:00";
    private static final Long USER_ID = 1L;

    private Date startDate;
    private Date endDate;

    @BeforeEach
    public void setUpAll() {
        MockitoAnnotations.openMocks(this);

        // Mock ClassModel
        classModel = ClassModel.builder()
                .classId(1L)
                .javerianaId(20001L)
                .period("2025-10")
                .numberOfParticipants(20)
                .build();

        // Mock Practice
        practice = Practice.builder()
                .id(1L)
                .name("Practice 1")
                .description("Descripción de la práctica")
                .type(PracticeType.GRUPAL)
                .gradeable(false)
                .classModel(classModel)
                .build();
        
        // Mock Course
        mockCourse1 = Course.builder()
                .courseId(1L)
                .javerianaId(20001L)
                .name("Cirugia General")
                .faculty("Medicina")
                .department("Cirugia")
                .program("Medicina")
                .semester(5)
                .build();

        // Mock Room Type
        mockRoomType = RoomType.builder().id(1L).name("Cirugia").build();

        // Mock Rooms
        mockRoom1 = Room.builder().id(1L).name("Room A").capacity(20).ip("10.43.100.23").type(mockRoomType).build();
        mockRoom2 = Room.builder().id(2L).name("Room B").capacity(15).ip("10.41.104.12").type(mockRoomType).build();

        rooms = List.of(mockRoom1, mockRoom2);

        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(START);
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(END);
            simulation = Simulation.builder()
                    .simulationId(1L)
                    .practice(practice)
                    .startDateTime(startDate)
                    .endDateTime(endDate)
                    .rooms(rooms)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        simulations = new ArrayList<>(List.of(simulation));

        // Set Relationships
        classModel.setCourse(mockCourse1);
    }

    @Test
    public void testSearchAllEvents_ReturnsEvents() {
        // Arrange
        when(simulationRepository.findByStartDateTimeBetween(startDate, endDate)).thenReturn(simulations);

        // Act
        List<EventDto> result = calendarService.searchAllEvents(USER_ID, START, END);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Practice 1 - Cirugia General (20001) - GRUPAL", result.get(0).getTitle());
        verify(simulationRepository, times(1)).findByStartDateTimeBetween(startDate, endDate);
    }

    @Test
    public void testSearchAllEvents_ReturnsEmptyList() {
        // Arrange
        when(simulationRepository.findByStartDateTimeBetween(startDate, endDate))
                .thenReturn(Collections.emptyList());

        // Act
        List<EventDto> result = calendarService.searchAllEvents(USER_ID, START, END);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(simulationRepository, times(1)).findByStartDateTimeBetween(startDate, endDate);
    }

    @Test
    public void testSearchEvents_ReturnsEventsForUser() {
        // Arrange
        when(simulationRepository.findByUsers_IdAndStartDateTimeBetween(USER_ID, startDate, endDate))
                .thenReturn(simulations);
        when(classModelRepository.findByProfessors_Id(USER_ID)).thenReturn(List.of(classModel));
        when(practiceRepository.findByClassModelIn(List.of(classModel))).thenReturn(List.of(practice));
        when(simulationRepository.findByPracticeInAndStartDateTimeBetween(List.of(practice), startDate, endDate))
                .thenReturn(simulations);

        // Act
        List<EventDto> result = calendarService.searchEvents(USER_ID, START, END);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Practice 1 - Cirugia General (20001) - GRUPAL", result.get(0).getTitle());
        verify(simulationRepository, times(1)).findByUsers_IdAndStartDateTimeBetween(USER_ID, startDate, endDate);
        verify(classModelRepository, times(1)).findByProfessors_Id(USER_ID);
        verify(practiceRepository, times(1)).findByClassModelIn(List.of(classModel));
        verify(simulationRepository, times(1)).findByPracticeInAndStartDateTimeBetween(List.of(practice), startDate,
                endDate);
    }

    @Test
    public void testSearchEvents_NoSimulationsOrClasses() {
        // Arrange
        when(simulationRepository.findByUsers_IdAndStartDateTimeBetween(USER_ID, startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(classModelRepository.findByProfessors_Id(USER_ID)).thenReturn(Collections.emptyList());

        // Act
        List<EventDto> result = calendarService.searchEvents(USER_ID, START, END);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(simulationRepository, times(1)).findByUsers_IdAndStartDateTimeBetween(USER_ID, startDate, endDate);
        verify(classModelRepository, times(1)).findByProfessors_Id(USER_ID);
    }

    @Test
    public void testSearchAllEvents_InvalidDateFormat() {
        // Act & Assert
        Exception exception = assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            calendarService.searchAllEvents(USER_ID, "invalid-date", END);
        });
        assertTrue(exception.getMessage().contains("Invalid date format"));
    }
}
