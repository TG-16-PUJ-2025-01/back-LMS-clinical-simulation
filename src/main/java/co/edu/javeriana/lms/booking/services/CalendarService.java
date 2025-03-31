package co.edu.javeriana.lms.booking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import lombok.extern.slf4j.Slf4j;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;

import co.edu.javeriana.lms.booking.dtos.EventDto;
import co.edu.javeriana.lms.booking.models.Room;
import jakarta.persistence.EntityNotFoundException;

@Slf4j
@Service
public class CalendarService {

    @Autowired
    private ClassRepository classModelRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<EventDto> searchAllEvents(Long idUser){
        // Retrieve all simulations in the system
        List<Simulation> simulations = simulationRepository.findAll();

        if (simulations.isEmpty()) {
            log.info("No simulations found. Returning an empty list.");
            return new ArrayList<>();
        }

        log.info("Simulations found: {}", simulations.size());
        return mapSimulationsToEventDtos(simulations);
    }
    
    public List<EventDto> searchEvents(Long idUser) {
        // Retrieve user to ensure it exists and fetch simulations directly associated
        // with the user
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + idUser));

        List<Simulation> userSimulations = user.getSimulations();
        log.info("Simulations found directly for user with id {}: {}", idUser, userSimulations.size());

        // Search classes where user is a professor
        List<ClassModel> classes = classModelRepository.findByProfessors_Id(idUser);
        if (classes.isEmpty()) {
            log.info("No classes found for user with id: {}. Returning only direct simulations.", idUser);
            if (userSimulations.isEmpty()) {
                log.info("No simulations found for user with id: {}. Returning an empty list.", idUser);
                return new ArrayList<>();
            }
            return mapSimulationsToEventDtos(userSimulations);
        }

        log.info("Classes found for user with id {}: {}", idUser, classes.size());

        // Search practices associated with those classes
        List<Practice> practices = practiceRepository.findByClassModelIn(classes);
        if (practices.isEmpty()) {
            log.info("No practices found for the classes of user with id: {}. Returning only direct simulations.",
                    idUser);
            if (userSimulations.isEmpty()) {
                log.info("No simulations found for user with id: {}. Returning an empty list.", idUser);
                return new ArrayList<>();
            }
            return mapSimulationsToEventDtos(userSimulations);
        }

        log.info("Practices found for user with id {}: {}", idUser, practices.size());

        // Search simulations associated with those practices
        List<Simulation> simulations = simulationRepository.findByPracticeIn(practices);

        simulations.addAll(userSimulations);

        if (simulations.isEmpty()) {
            log.info("No simulations found for user with id: {}. Returning an empty list.", idUser);
            return new ArrayList<>();
        }

        // Remove duplicates
        List<Simulation> distinctSimulations = simulations.stream().distinct().collect(Collectors.toList());

        return mapSimulationsToEventDtos(distinctSimulations);
    }

    private String getRoomNames(List<Room> rooms) {
        return rooms != null && !rooms.isEmpty()
            ? rooms.stream().map(Room::getName).collect(Collectors.joining(", "))
            : "Sin sala";
    }

    private List<EventDto> mapSimulationsToEventDtos(List<Simulation> simulations) {
        List<EventDto> eventsDtos = new ArrayList<>();
        
        for (Simulation simulation : simulations) {
            Practice practice = simulation.getPractice();
            ClassModel classModel = practice.getClassModel();
            String calendarId = simulation.getPractice() == null ? "Supervisor" : "Reserva";
            EventDto eventDto = EventDto.builder()
                    .id(simulation.getSimulationId())
                    .title(practice.getName() + " - " + classModel.getCourse().getName() + " - " + practice.getType().name())
                    .description("Clase: " + classModel.getCourse().getName() + " - " + classModel.getJaverianaId())
                    .location(getRoomNames(simulation.getRooms()))
                    .start(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                            simulation.getStartDateTime()))
                    .end(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                            simulation.getEndDateTime()))
                    .calendarId(calendarId)
                    .build();
            eventsDtos.add(eventDto);
        }

        return eventsDtos;
    }
}
