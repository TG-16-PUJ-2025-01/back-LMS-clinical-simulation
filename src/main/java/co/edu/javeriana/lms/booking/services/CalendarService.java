package co.edu.javeriana.lms.booking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import lombok.extern.slf4j.Slf4j;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;

import co.edu.javeriana.lms.booking.dtos.EventDto;
import co.edu.javeriana.lms.booking.models.Room;

@Slf4j
@Service
public class CalendarService {

    @Autowired
    private ClassRepository classModelRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    public List<EventDto> searchAllEvents(Long idUser, String start, String end) {
        Date startDate = parseDate(start, "yyyy-MM-dd HH:mm");
        Date endDate = parseDate(end, "yyyy-MM-dd HH:mm");

        List<Simulation> simulations = simulationRepository.findByStartDateTimeBetween(startDate, endDate);

        if (simulations.isEmpty()) {
            log.info("No simulations found between {} and {}. Returning an empty list.", start, end);
            return new ArrayList<>();
        }

        log.info("Number of simulations found between {} and {}: {}", start, end, simulations.size());
        return mapSimulationsToEventDtos(simulations);
    }

    public List<EventDto> searchEvents(Long idUser, String start, String end) {
        Date startDate = parseDate(start, "yyyy-MM-dd HH:mm");
        Date endDate = parseDate(end, "yyyy-MM-dd HH:mm");

        // Fetch simulations directly associated with the user and within the date range
        List<Simulation> userSimulations = simulationRepository.findByUsers_IdAndStartDateTimeBetween(idUser, startDate,
                endDate);

        log.info("Simulations found directly for user with id {}: {}", idUser, userSimulations.size());

        // Search classes where user is a professor
        List<ClassModel> classes = classModelRepository.findByProfessors_Id(idUser);
        if (classes.isEmpty()) {
            if (userSimulations.isEmpty()) {
                log.info("No simulations found between {} and {}. Returning an empty list.", start, end);
                return new ArrayList<>();
            }
            return mapSimulationsToEventDtos(userSimulations);
        }

        log.info("Classes found for user with id {}: {}", idUser, classes.size());

        // Search practices associated with those classes
        List<Practice> practices = practiceRepository.findByClassModelIn(classes);
        if (practices.isEmpty()) {
            if (userSimulations.isEmpty()) {
                log.info("No simulations found between {} and {}. Returning an empty list.", start, end);
                return new ArrayList<>();
            }
            log.info("Number of simulations found between {} and {}: {}", start, end, userSimulations.size());
            return mapSimulationsToEventDtos(userSimulations);
        }

        log.info("Practices found for user with id {}: {}", idUser, practices.size());

        // Search simulations associated with those practices and within the date range
        List<Simulation> simulations = simulationRepository.findByPracticeInAndStartDateTimeBetween(practices,
                startDate, endDate);

        simulations.addAll(userSimulations);

        if (simulations.isEmpty()) {
            log.info("No simulations found between {} and {}. Returning an empty list.", start, end);
            return new ArrayList<>();
        }

        // Remove duplicates
        List<Simulation> distinctSimulations = new ArrayList<>(
                simulations.stream()
                        .collect(Collectors.toMap(
                                Simulation::getSimulationId,
                                s -> s,
                                (s1, s2) -> s1,
                                java.util.LinkedHashMap::new
                        ))
                        .values());

        log.info("Number of simulations found between {} and {}: {}", start, end, distinctSimulations.size());
        return mapSimulationsToEventDtos(distinctSimulations);
    }

    private Date parseDate(String date, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(date);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid date format. Expected format: " + format);
        }
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
                    .title(practice.getName() + " - " + classModel.getCourse().getName() + " (" + classModel.getJaverianaId() + ") - "
                            + practice.getType().name())
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
