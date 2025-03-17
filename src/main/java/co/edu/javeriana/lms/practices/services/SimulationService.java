package co.edu.javeriana.lms.practices.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Simulation> findSimulationsByPracticeId(Long practiceId, Integer page, Integer size) {
        practiceRepository.findById(practiceId)
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + practiceId));

        // TODO filtros
        Pageable pageable = PageRequest.of(page, size);
        return simulationRepository.findByPracticeId(practiceId, pageable);
    }

    @Transactional
    public List<Simulation> addSimulations(List<SimulationByTimeSlotDto> simulationsDto) {

        if (!enoughSimulations(simulationsDto)) {
            throw new IllegalArgumentException("Not enough simulations for all the students");
        }
        List<Simulation> createdSimulations = new ArrayList<>();
        for (SimulationByTimeSlotDto simulation : simulationsDto) {
            createdSimulations.addAll(addSimulationsPerTimeSlot(simulation));
        }
        log.info("Simulations created: {}", createdSimulations.size());
        return createdSimulations;
    }

    private boolean enoughSimulations(List<SimulationByTimeSlotDto> simulationsDto) {
        Practice practice = practiceRepository.findById(simulationsDto.get(0).getPracticeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Practice not found with id: " + simulationsDto.get(0).getPracticeId()));
        Integer duration = practice.getSimulationDuration();
        // TODO corregir
        Integer numberOfGroups = practice.getMaxStudentsGroup();

        int totalSimulationsAvailable = 0;

        for (SimulationByTimeSlotDto simulation : simulationsDto) {
            long durationInMinutes = java.time.Duration
                    .between(simulation.getStartDateTime(), simulation.getEndDateTime()).toMinutes();
            totalSimulationsAvailable += durationInMinutes / duration;
        }

        return totalSimulationsAvailable >= numberOfGroups;
    }

    @Transactional
    private List<Simulation> addSimulationsPerTimeSlot(SimulationByTimeSlotDto simulationDto) {

        Practice practice = practiceRepository.findById(simulationDto.getPracticeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Practice not found with id: " + simulationDto.getPracticeId()));

        Room room = roomRepository.findById(simulationDto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + simulationDto.getRoomId()));

        if (!simulationRepository.isRoomAvailable(room, simulationDto.getStartDateTime(),
                simulationDto.getEndDateTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for the selected dates");
        }

        if (practice.getMaxStudentsGroup() > room.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room capacity is not enough for the selected practice");
        }

        Integer duration = practice.getSimulationDuration();
        
        GradeStatus gradeStatus = practice.getGradeable() ? GradeStatus.PENDING : GradeStatus.NOT_EVALUABLE;

        List<Simulation> createdSimulations = new ArrayList<>();

        while (simulationDto.getStartDateTime().isBefore(simulationDto.getEndDateTime())) {
            Simulation simulation = Simulation.builder()
                    .practice(practice)
                    .room(room)
                    .startDateTime(simulationDto.getStartDateTime())
                    .endDateTime(simulationDto.getStartDateTime().plusMinutes(duration))
                    .gradeDateTime(null)
                    .gradeStatus(gradeStatus)
                    .grade(null)
                    .build();
            createdSimulations.add(simulation);
            simulationRepository.save(simulation);
            simulationDto.setStartDateTime(simulationDto.getStartDateTime().plusMinutes(duration));
        }

        return createdSimulations;
    }

    public Simulation updateSimulation(Long id, SimulationDto simulationDto) {
        Simulation existingSimulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        Practice practice = practiceRepository.findById(simulationDto.getPracticeId()).orElseThrow(
                () -> new EntityNotFoundException("Practice not found with id: " + simulationDto.getPracticeId()));
        Room room = roomRepository.findById(simulationDto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + simulationDto.getRoomId()));

        if (!simulationRepository.isRoomAvailable(room, simulationDto.getStartDateTime(),
                simulationDto.getEndDateTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for the selected dates");
        }

        if (practice.getMaxStudentsGroup() > room.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room capacity is not enough for the selected practice");
        }

        existingSimulation.setPractice(practice);
        existingSimulation.setRoom(room);
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

    public List<TimeSlotDto> findRoomSimulationsSchedule(Long roomId, LocalDate startOfWeekDate) {
        roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + roomId));
        LocalDateTime startOfWeek = startOfWeekDate.atStartOfDay();
        LocalDateTime endOfWeek = startOfWeekDate.plusDays(7).atStartOfDay().minusSeconds(1);
        List<Simulation> simulations = simulationRepository.findByRoomIdAndStartDateTimeBetween(roomId, startOfWeek,
                endOfWeek);
        
        List<TimeSlotDto> timeSlots = new ArrayList<>();
        for (Simulation simulation : simulations) {
            timeSlots.add(TimeSlotDto.builder()
                    .startDateTime(simulation.getStartDateTime())
                    .endDateTime(simulation.getEndDateTime())
                    .build());
        }

        return timeSlots;
    }

    public List<User> findSimulationStudents(Long simulationId) {
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + simulationId));

        return simulation.getUsers();
    }
}
