package co.edu.javeriana.lms.booking.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
                throw new EntityNotFoundException("No simulations found for user with id: " + idUser);
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
                throw new EntityNotFoundException("No simulations found for user with id: " + idUser);
            }
            return mapSimulationsToEventDtos(userSimulations);
        }

        log.info("Practices found for user with id {}: {}", idUser, practices.size());

        // Search simulations associated with those practices
        List<Simulation> simulations = simulationRepository.findByPracticeIn(practices);
        simulations.addAll(userSimulations);

        if (simulations.isEmpty()) {
            throw new EntityNotFoundException("No simulations found for user with id: " + idUser);
        }

        // Remove duplicates
        List<Simulation> distinctSimulations = simulations.stream().distinct().collect(Collectors.toList());

        return mapSimulationsToEventDtos(distinctSimulations);
    }

    private List<EventDto> mapSimulationsToEventDtos(List<Simulation> simulations) {
        // TODO se debe implementar la lógica para reservas con multiples salas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<EventDto> eventsDtos = new ArrayList<>();
        
        for (Simulation simulation : simulations) {
            Practice practice = simulation.getPractice();
            ClassModel classModel = practice.getClassModel();
            EventDto eventDto = EventDto.builder()
                    .id(simulation.getSimulationId())
                    .title(practice.getName())
                    .description("Clase: " + classModel.getCourse().getName())
                    .location(simulation.getRooms() != null ? simulation.getRooms().get(0).getName() : "Sin sala")
                    .start(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                            simulation.getStartDateTime()))
                    .end(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                            simulation.getEndDateTime()))
                    .build();
            eventsDtos.add(eventDto);
        }

        return eventsDtos;
    }
}
