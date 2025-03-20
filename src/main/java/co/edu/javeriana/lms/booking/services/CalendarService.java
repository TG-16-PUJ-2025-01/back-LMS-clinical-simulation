package co.edu.javeriana.lms.booking.services;

import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
        // Get the current date and determine the semester
        LocalDate now = LocalDate.now();
        LocalDateTime semesterStart;
        LocalDateTime semesterEnd;

        if (now.getMonthValue() <= 6) {
            semesterStart = LocalDateTime.of(now.getYear(), 1, 1, 0, 0);
            semesterEnd = LocalDateTime.of(now.getYear(), 6, 30, 23, 59);
        } else {
            semesterStart = LocalDateTime.of(now.getYear(), 7, 1, 0, 0);
            semesterEnd = LocalDateTime.of(now.getYear(), 12, 31, 23, 59);
        }

        log.info("Semester start: {}", semesterStart);
        log.info("Semester end: {}", semesterEnd);

        // Check if the user exists
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + idUser));

        // Search simulations directly for the user within the semester
        List<Simulation> userSimulations = simulationRepository.findByPracticeInAndStartDateTimeBetween(
                user.getSimulations().stream().map(Simulation::getPractice).collect(Collectors.toList()),
                semesterStart, semesterEnd);

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

        // Search practices associated with those classes within the semester
        List<Practice> practices = practiceRepository.findByClassModelInAndSimulations_StartDateTimeBetween(
                classes, semesterStart, semesterEnd);

        if (practices.isEmpty()) {
            log.info("No practices found for the classes of user with id: {}. Returning only direct simulations.",
                    idUser);
            if (userSimulations.isEmpty()) {
                throw new EntityNotFoundException("No simulations found for user with id: " + idUser);
            }
            return mapSimulationsToEventDtos(userSimulations);
        }

        log.info("Practices found for user with id {}: {}", idUser, practices.size());

        // Search simulations associated with those practices within the semester
        List<Simulation> simulations = simulationRepository.findByPracticeInAndStartDateTimeBetween(
                practices, semesterStart, semesterEnd);
        simulations.addAll(userSimulations);

        if (simulations.isEmpty()) {
            throw new EntityNotFoundException("No simulations found for user with id: " + idUser);
        }

        // Remove duplicates
        List<Simulation> distinctSimulations = simulations.stream().distinct().collect(Collectors.toList());

        return mapSimulationsToEventDtos(distinctSimulations);
    }

    private List<EventDto> mapSimulationsToEventDtos(List<Simulation> simulations) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return simulations.stream().map(simulation -> {
            Practice practice = simulation.getPractice();
            ClassModel classModel = practice.getClassModel();
            return new EventDto(
                    simulation.getId().intValue(),
                    practice.getName(),
                    "Clase: " + classModel.getCourse().getName(),
                    simulation.getRoom() != null ? simulation.getRoom().getName() : "Sin sala",
                    simulation.getStartDateTime().format(formatter),
                    simulation.getEndDateTime().format(formatter),
                    "Reserva");
        }).collect(Collectors.toList());
    }
}
