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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.practices.dtos.SimulationByTimeSlotDto;
import co.edu.javeriana.lms.practices.dtos.SimulationDto;
import co.edu.javeriana.lms.practices.dtos.TimeSlotDto;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.grades.models.GradeStatus;
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

    public Page<Simulation> findAllSimulations(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return simulationRepository.findAll(pageable);
    }

    public Simulation findSimulationById(Long id) {
        return simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));
    }

    public Page<Simulation> findSimulationsByPracticeId(Long practiceId, Integer page, Integer size, String sort, Boolean asc, Integer groupNumber) {
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

    public Simulation updateSimulation(Long id, SimulationDto simulationDto) {
        Simulation existingSimulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        Practice practice = practiceRepository.findById(simulationDto.getPracticeId()).orElseThrow(
                () -> new EntityNotFoundException("Practice not found with id: " + simulationDto.getPracticeId()));

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

            if (practice.getMaxStudentsGroup() > room.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Room capacity is not enough for the selected practice");
            }
        }

        existingSimulation.setPractice(practice);
        existingSimulation.setRooms(rooms);
        existingSimulation.setStartDateTime(simulationDto.getStartDateTime());
        existingSimulation.setEndDateTime(simulationDto.getEndDateTime());
        existingSimulation.setGradeDateTime(simulationDto.getGradeDateTime());
        existingSimulation.setGradeStatus(simulationDto.getGradeStatus());
        existingSimulation.setGrade(simulationDto.getGrade());
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

    private Date parseDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(date);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
        }
    }
}
