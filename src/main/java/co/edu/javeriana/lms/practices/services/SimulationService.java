package co.edu.javeriana.lms.practices.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.services.UserService;
import co.edu.javeriana.lms.booking.services.RoomService;
import co.edu.javeriana.lms.practices.dtos.SimulationDto;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.booking.models.Room;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimulationService {

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    public Page<Simulation> findAllSimulations(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return simulationRepository.findAll(pageable);
    }

    public Simulation findSimulationById(Long id) {
        return simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));
    }

    public Page<Simulation> findSimulationsByPracticeId(Long practiceId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return simulationRepository.findByPracticeId(practiceId, pageable);
    }

    public void addSimulations(List<SimulationDto> simulationsDto) {
        
         // TODO Change this to a custom exception
        if (!enoughSimulations(simulationsDto)) {
            throw new RuntimeException("Not enough simulations for all the students");
        }

        for (SimulationDto simulation : simulationsDto) {
            addSimulationsPerTimeSlot(simulation);
        }
    }

    private boolean enoughSimulations(List<SimulationDto> simulationsDto) {
        Practice practice = practiceRepository.findById(simulationsDto.get(0).getPracticeId())
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + simulationsDto.get(0).getPracticeId()));
        Integer duration = practice.getSimulationDuration();
        Integer numberOfGroups = practice.getMaxStudentsGroup();

        int totalSimulationsAvailable = 0;

        for (SimulationDto simulation : simulationsDto) {
            long durationInMinutes = java.time.Duration.between(simulation.getStartDateTime(), simulation.getEndDateTime()).toMinutes();
            totalSimulationsAvailable += durationInMinutes / duration;
        }

        return totalSimulationsAvailable >= numberOfGroups;
    }


    private void addSimulationsPerTimeSlot(SimulationDto simulationDto) {

        Practice practice = practiceRepository.findById(simulationDto.getPracticeId())
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + simulationDto.getPracticeId()));

        Room room = roomService.findById(simulationDto.getRoomId());
        if (room == null) {
            throw new EntityNotFoundException("Room not found with id: " + simulationDto.getRoomId());
        }

        // TODO Change this to a custom exception
        if (!simulationRepository.isRoomAvailable(room, simulationDto.getStartDateTime(), simulationDto.getEndDateTime())) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        if (practice.getMaxStudentsGroup() > room.getCapacity()) {
            throw new RuntimeException("Room capacity is not enough for the selected practice");
        }

        Integer duration = practice.getSimulationDuration();
        
        while (simulationDto.getStartDateTime().isBefore(simulationDto.getEndDateTime())) {
            Simulation simulation = Simulation.builder()
                    .practice(practice)
                    .room(room)
                    .startDateTime(simulationDto.getStartDateTime())
                    .endDateTime(simulationDto.getStartDateTime().plusMinutes(duration))
                    .gradeDate(null)
                    .gradeStatus(null)
                    .grade(null)
                    .build();
            simulationRepository.save(simulation);
            simulationDto.setStartDateTime(simulationDto.getStartDateTime().plusMinutes(duration));
        }
    }

    /*
    public Simulation addSimulation(SimulationDto simulationDto) {
        Practice practice = practiceService.findById(simulationDto.getPracticeId());
        Room room = roomService.findById(simulationDto.getRoomId());

        // TODO Change this to a custom exception
        if (!simulationRepository.isRoomAvailable(room, simulationDto.getStartDateTime(), simulationDto.getEndDateTime())) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        if (practice.getMaxStudentsGroup() > room.getCapacity()) {
            throw new RuntimeException("Room capacity is not enough for the selected practice");
        }

        Simulation simulation = Simulation.builder()
                .practice(practice)
                .room(room)
                .startDateTime(simulationDto.getStartDateTime())
                .endDateTime(simulationDto.getEndDateTime())
                .build();
        
        
        return simulationRepository.save(simulation);
    }*/

    public Simulation updateSimulation(Long id, SimulationDto simulationDto) {
        Simulation existingSimulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        Practice practice = practiceRepository.findById(simulationDto.getPracticeId()).orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + simulationDto.getPracticeId()));
        Room room = roomService.findById(simulationDto.getRoomId());

        // TODO Change this to a custom exception
        if (!simulationRepository.isRoomAvailable(room, simulationDto.getStartDateTime(), simulationDto.getEndDateTime())) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        if (practice.getMaxStudentsGroup() > room.getCapacity()) {
            throw new RuntimeException("Room capacity is not enough for the selected practice");
        }

        existingSimulation.setPractice(practice);
        existingSimulation.setRoom(room);
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

        simulation.getUsers().add(userService.findById(studentId));
        simulationRepository.save(simulation);
    }
}
