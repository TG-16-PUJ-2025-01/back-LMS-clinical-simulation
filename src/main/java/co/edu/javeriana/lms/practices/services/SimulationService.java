package co.edu.javeriana.lms.practices.services;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.practices.dtos.SimulationByTimeSlotDto;
import co.edu.javeriana.lms.practices.dtos.TimeSlotDto;
import co.edu.javeriana.lms.practices.dtos.SimulationAvailabilityDto;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.grades.dtos.RubricDto;
import co.edu.javeriana.lms.grades.models.GradeStatus;
import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimulationService {

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RubricRepository rubricRepository;

    public Page<Simulation> findAllSimulations(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return simulationRepository.findAll(pageable);
    }

    public Simulation findSimulationById(Long id) {
        return simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));
    }

    public Page<Simulation> findSimulationsByPracticeId(Long practiceId, Integer page, Integer size, String sort,
            Boolean asc, Integer groupNumber) {
        practiceRepository.findById(practiceId)
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + practiceId));

        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        if (groupNumber == null) {
            return simulationRepository.findByPracticeId(practiceId, pageable);
        } else {
            return simulationRepository.findByPracticeIdAndGroupNumber(practiceId, groupNumber, pageable);
        }
    }

    @Transactional
    public List<Simulation> addSimulations(List<SimulationByTimeSlotDto> simulationsDto) {

        if (!canAccommodateAllGroups(simulationsDto)) {
            throw new IllegalArgumentException(
                    "The number of groups does not match the number of available time slots");
        }
        List<Simulation> createdSimulations = new ArrayList<>();
        for (SimulationByTimeSlotDto simulation : simulationsDto) {
            createdSimulations.addAll(addSimulationsPerTimeSlot(simulation));
        }
        log.info("Simulations created: {}", createdSimulations.size());
        return createdSimulations;
    }

    private boolean canAccommodateAllGroups(List<SimulationByTimeSlotDto> simulationsDto) {
        Practice practice = practiceRepository.findById(simulationsDto.get(0).getPracticeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Practice not found with id: " + simulationsDto.get(0).getPracticeId()));
        Integer duration = practice.getSimulationDuration();
        Integer numberOfGroups = practice.getNumberOfGroups();

        int totalSimulationsAvailable = 0;

        for (SimulationByTimeSlotDto simulation : simulationsDto) {
            long durationInMinutes = java.time.Duration
                    .between(simulation.getStartDateTime().toInstant(), simulation.getEndDateTime().toInstant())
                    .toMinutes();
            totalSimulationsAvailable += durationInMinutes / duration;
        }

        return totalSimulationsAvailable == numberOfGroups;
    }

    @Transactional
    private List<Simulation> addSimulationsPerTimeSlot(SimulationByTimeSlotDto simulationDto) {

        Practice practice = practiceRepository.findById(simulationDto.getPracticeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Practice not found with id: " + simulationDto.getPracticeId()));

        List<Room> rooms = new ArrayList<>();
        for (Long roomId : simulationDto.getRoomIds()) {
            rooms.add(roomRepository.findById(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + roomId)));
        }

        for (Room room : rooms) {
            if (!simulationRepository.isRoomAvailable(room, simulationDto.getStartDateTime(),
                    simulationDto.getEndDateTime())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for the selected dates");
            }

            if (practice.getMaxStudentsGroup() > room.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Room capacity is not enough for the selected practice");
            }
        }

        Integer duration = practice.getSimulationDuration();

        GradeStatus gradeStatus = practice.getGradeable() ? GradeStatus.PENDING : GradeStatus.NOT_EVALUABLE;

        Integer lastGroupNumber = simulationRepository.findMaxGroupNumberByPracticeId(practice.getId());

        if (lastGroupNumber == null) {
            lastGroupNumber = 0;
        }

        lastGroupNumber++;

        List<Simulation> createdSimulations = new ArrayList<>();

        while (simulationDto.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .isBefore(
                        simulationDto.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            Simulation simulation = Simulation.builder()
                    .practice(practice)
                    .rooms(rooms)
                    .startDateTime(simulationDto.getStartDateTime())
                    .endDateTime(Date.from(simulationDto.getStartDateTime().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDateTime().plusMinutes(duration).atZone(ZoneId.systemDefault()).toInstant()))
                    .gradeDateTime(null)
                    .gradeStatus(gradeStatus)
                    .grade(null)
                    .groupNumber(lastGroupNumber)
                    .build();
            createdSimulations.add(simulation);
            simulationRepository.save(simulation);
            simulationDto.setStartDateTime(Date.from(simulationDto.getStartDateTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(duration)
                    .atZone(ZoneId.systemDefault()).toInstant()));

            lastGroupNumber++;
        }

        return createdSimulations;
    }

    public Simulation updateSimulation(Long id, SimulationByTimeSlotDto simulationDto) {
        Simulation existingSimulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        List<Room> rooms = new ArrayList<>();

        for (Long roomId : simulationDto.getRoomIds()) {
            rooms.add(roomRepository.findById(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + roomId)));
        }

        existingSimulation.setStartDateTime(null);
        existingSimulation.setEndDateTime(null);

        simulationRepository.save(existingSimulation);

        for (Room room : existingSimulation.getRooms()) {
            if (!simulationRepository.isRoomAvailable(room, simulationDto.getStartDateTime(),
                    simulationDto.getEndDateTime())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for the selected dates");
            }

            if (existingSimulation.getPractice().getMaxStudentsGroup() > room.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Room capacity is not enough for the selected practice");
            }
        }

        existingSimulation.setRooms(rooms);
        existingSimulation.setStartDateTime(simulationDto.getStartDateTime());
        existingSimulation.setEndDateTime(simulationDto.getEndDateTime());
        return simulationRepository.save(existingSimulation);
    }

    public void deleteSimulationById(Long id) {
        if (!simulationRepository.existsById(id)) {
            throw new EntityNotFoundException("Simulation not found with id: " + id);
        }
        simulationRepository.deleteById(id);
    }

    public void addStudentToSimulation(Long simulationId, Long studentId) {
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + simulationId));

        simulation.getUsers().add(userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + studentId)));
        simulationRepository.save(simulation);
    }

    public List<TimeSlotDto> findSimulationsSchedule(String date) {
        Date startDate = parseDate(date);
        Date endDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000);

        List<Simulation> simulations = simulationRepository.findByStartDateTimeBetween(startDate, endDate);

        List<TimeSlotDto> timeSlots = new ArrayList<>();

        if (simulations.isEmpty()) {
            return timeSlots;
        }

        // Mapa para agrupar simulaciones por cada roomId
        Map<Long, List<Simulation>> simulationsByRoom = new HashMap<>();

        for (Simulation simulation : simulations) {
            for (Room room : simulation.getRooms()) { // Iteramos sobre todas las salas de la simulación
                simulationsByRoom
                        .computeIfAbsent(room.getId(), k -> new ArrayList<>())
                        .add(simulation);
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Procesar cada grupo de simulaciones por roomId
        for (Map.Entry<Long, List<Simulation>> entry : simulationsByRoom.entrySet()) {
            List<Simulation> roomSimulations = entry.getValue();
            roomSimulations.sort(Comparator.comparing(Simulation::getStartDateTime));

            Date unifiedStart = roomSimulations.get(0).getStartDateTime();
            Date unifiedEnd = roomSimulations.get(0).getEndDateTime();
            String roomName = roomSimulations.get(0).getRooms().stream()
                    .filter(r -> r.getId().equals(entry.getKey()))
                    .findFirst()
                    .map(Room::getName)
                    .orElse("Unknown Room");

            for (int i = 1; i < roomSimulations.size(); i++) {
                Simulation currentSimulation = roomSimulations.get(i);

                if (!currentSimulation.getStartDateTime().after(unifiedEnd)) {
                    // Se solapan o son contiguos dentro del mismo roomId
                    unifiedEnd = unifiedEnd.after(currentSimulation.getEndDateTime()) ? unifiedEnd
                            : currentSimulation.getEndDateTime();
                } else {
                    // Guardar el intervalo previo antes de actualizar
                    timeSlots.add(TimeSlotDto.builder()
                            .room(roomName)
                            .startDateTime(dateFormat.format(unifiedStart))
                            .endDateTime(dateFormat.format(unifiedEnd))
                            .build());

                    unifiedStart = currentSimulation.getStartDateTime();
                    unifiedEnd = currentSimulation.getEndDateTime();
                }
            }

            // Agregar el último intervalo del roomId actual
            timeSlots.add(TimeSlotDto.builder()
                    .room(roomName)
                    .startDateTime(dateFormat.format(unifiedStart))
                    .endDateTime(dateFormat.format(unifiedEnd))
                    .build());
        }

        return timeSlots;
    }

    public List<User> findSimulationStudents(Long simulationId) {
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + simulationId));

        return simulation.getUsers();
    }

    public Rubric updateSimulationRubric(Long id, RubricDto rubricDto) {
        Simulation simulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        Rubric rubric = rubricDto.toRubric();
        Rubric existingRubric = simulation.getRubric();
        if (existingRubric != null) {
            existingRubric.setEvaluatedCriterias(rubric.getEvaluatedCriterias());
            existingRubric.setTotal(rubric.getTotal());
            rubricRepository.save(existingRubric);
        } else {
            rubric.setSimulation(simulation);
            rubric = rubricRepository.save(rubric);
            simulation.setRubric(rubric);
            simulationRepository.save(simulation);
        }

        return rubric;
    }

    public Simulation publishGrade(Long id) {
        Simulation simulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        simulation.setGradeStatus(GradeStatus.REGISTERED);
        simulation.setGradeDateTime(new Date());
        simulation.setGrade(simulation.getRubric().getTotal().getScore());
        return simulationRepository.save(simulation);
    }

    private Date parseDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(date);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
        }
    }

    public void joinSimulation(Long id, Long userId) {
        // Check if the simulation exists
        Simulation simulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        // Check if the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check if the user is not already enrolled in the simulation
        if (simulation.getUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User is already enrolled in the simulation, cannot join group");
        }

        // Check if the simulation is not full
        if (simulation.getUsers().size() >= simulation.getPractice().getMaxStudentsGroup()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Simulation is full, cannot join group");
        }

        // Check if the simulation has not already started
        if (simulation.getStartDateTime() != null && simulation.getEndDateTime() != null) {
            Date now = new Date();
            if (now.after(simulation.getStartDateTime()) && now.before(simulation.getEndDateTime())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Simulation is in progress, cannot join group");
            }
        }

        // Check if the simulation already happened
        if (simulation.getEndDateTime() != null) {
            Date now = new Date();
            if (now.after(simulation.getEndDateTime())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Simulation has already happened, cannot join group");
            }
        }

        // Check if the simulation is already graded
        if (simulation.getGradeStatus() == GradeStatus.REGISTERED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Simulation is already graded, cannot join group");
        }

        // Check if the user is already enrolled on another simulation
        simulation.getPractice().getSimulations().forEach(s -> {
            if (s.getUsers().contains(user)) {
                // Remove the user from the simulation
                s.getUsers().remove(user);
                simulationRepository.save(s);
                log.info("User {} removed from simulation {}", user.getId(), s.getSimulationId());
            }
        });

        // Add the user to the simulation
        simulation.getUsers().add(user);
        simulationRepository.save(simulation);
        log.info("User {} added to simulation {}", user.getId(), simulation.getSimulationId());
    }

    public Page<SimulationAvailabilityDto> findAvailableSimulationsByPracticeId(Long practiceId, Integer page,
            Integer size, String sort, Boolean asc, Integer groupNumber) {

        practiceRepository.findById(practiceId)
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + practiceId));

        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<Simulation> simulations;
        if (groupNumber == null) {
            simulations = simulationRepository.findByPracticeId(practiceId, pageable);
        } else {
            simulations = simulationRepository.findByPracticeIdAndGroupNumber(practiceId, groupNumber, pageable);
        }

        List<SimulationAvailabilityDto> simulationDtos = mapSimulationsToAvailabilityDtos(simulations.getContent());

        return new PageImpl<>(simulationDtos, pageable, simulations.getTotalElements());
    }

    private List<SimulationAvailabilityDto> mapSimulationsToAvailabilityDtos(List<Simulation> simulations) {
        return simulations.stream().map(simulation -> {
            boolean isFull = simulation.getUsers().size() >= simulation.getPractice().getMaxStudentsGroup();
            boolean hasStarted = simulation.getStartDateTime() != null && new Date().after(simulation.getStartDateTime());
            boolean isGraded = simulation.getGradeStatus() == GradeStatus.REGISTERED;

            boolean available = !isFull && !hasStarted && !isGraded;

            return SimulationAvailabilityDto.builder()
                    .simulationId(simulation.getSimulationId())
                    .groupNumber(simulation.getGroupNumber())
                    .startDateTime(simulation.getStartDateTime())
                    .endDateTime(simulation.getEndDateTime())
                    .available(available)
                    .build();
        }).toList();
    }
}