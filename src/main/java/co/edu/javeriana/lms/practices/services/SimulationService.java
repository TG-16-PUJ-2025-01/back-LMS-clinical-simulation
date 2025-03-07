package co.edu.javeriana.lms.practices.services;

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
    private PracticeService practiceService;

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
    }

    public Simulation updateSimulation(Long id, SimulationDto simulationDto) {
        Simulation existingSimulation = simulationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found with id: " + id));

        Practice practice = practiceService.findById(simulationDto.getPracticeId());
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
