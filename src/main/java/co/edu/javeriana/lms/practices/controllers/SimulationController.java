package co.edu.javeriana.lms.practices.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.practices.dtos.SimulationByTimeSlotDto;
import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.practices.dtos.CreateSimulationRequestDto;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.services.SimulationService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllSimulations(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Requesting all simulations");


        Page<Simulation> simulationsPage = simulationService.findAllSimulations(page, size);

        if (simulationsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No simulations found", null, null));
        }

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, simulationsPage.getNumberOfElements(),
                simulationsPage.getTotalElements(), simulationsPage.getTotalPages());

        return ResponseEntity.ok(
                new ApiResponseDto<List<Simulation>>(HttpStatus.OK.value(), "ok", simulationsPage.getContent(),
                        metadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getSimulationById(@PathVariable Long id) {
        log.info("Requesting simulation with id: {}", id);

        Simulation simulation = simulationService.findSimulationById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", simulation, null));
    }

    @GetMapping("/practice/{practiceId}")
    public ResponseEntity<ApiResponseDto<?>> getSimulationsByPracticeId(
            @PathVariable Long practiceId,
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "simulationId") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(required = false) Integer groupNumber) {
        log.info("Requesting simulations for practice with id: {}, groupNumber: {}", practiceId, groupNumber);

        Page<Simulation> simulationsPage = simulationService.findSimulationsByPracticeId(practiceId, page, size, sort, asc, groupNumber);

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, simulationsPage.getNumberOfElements(),
                simulationsPage.getTotalElements(), simulationsPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", simulationsPage.getContent(), metadata));
    }

    @PostMapping()
    public ResponseEntity<ApiResponseDto<?>> createSimulations(@Valid @RequestBody CreateSimulationRequestDto simulationRequestDto) {
        log.info("Creating simulations {} ", simulationRequestDto.getSimulations().size());
        List<SimulationByTimeSlotDto> SimulationsDto = new ArrayList<>();
        for (int i = 0; i < simulationRequestDto.getSimulations().size(); i++) {
            SimulationsDto.add(simulationRequestDto.getSimulations().get(i));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(HttpStatus.CREATED.value(), "Simulations created successfully", simulationService.addSimulations(SimulationsDto), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateSimulation(@PathVariable Long id,
            @Valid @RequestBody SimulationByTimeSlotDto simulationDto) {
        log.info("Updating simulation with id: {}", id);

        Simulation simulation = simulationService.updateSimulation(id, simulationDto);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Simulation updated successfully", simulation, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteSimulation(@Min(1) @RequestParam Long id) {
        log.info("Deleting simulation with id: {}", id);

        simulationService.deleteSimulationById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Simulation deleted successfully", null, null));
    }

    @GetMapping("/schedule")
    public ResponseEntity<ApiResponseDto<?>> getSchedule(@RequestParam String date) {
        log.info("Requesting simulations to show schedule for date: {}", date);

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", simulationService.findSimulationsSchedule(date), null));
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<ApiResponseDto<?>> getSimulationStudents(@PathVariable Long id) {
        log.info("Requesting simulation students with id: {}", id);

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", simulationService.findSimulationStudents(id), null));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponseDto<?>> joinSimulation(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        token = token.substring(7);
        log.info("Requesting to join simulation with id: {}", id);

        Long userId = authService.getUserIdByToken(token);

        log.info("User ID: {}", userId);

        // Join the simulation
        simulationService.joinSimulation(id, userId);

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", null, null));
    }
}