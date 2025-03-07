package co.edu.javeriana.lms.practices.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.practices.dtos.SimulationDto;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.services.SimulationService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllSimulations(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Requesting all simulations");

        String host = request.getHeader("Host");
        String scheme = request.getScheme();

        Page<Simulation> simulationsPage = simulationService.findAllSimulations(page, size);

        if (simulationsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No simulations found", null, null));
        }

        String previous = null;
        if (simulationsPage.hasPrevious()) {
            previous = String.format("%s://%s/simulation/all?page=%d&size=%d", scheme, host, page - 1, size);
        }

        String next = null;
        if (simulationsPage.hasNext()) {
            next = String.format("%s://%s/simulation/all?page=%d&size=%d", scheme, host, page + 1, size);
        }

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, simulationsPage.getNumberOfElements(),
                simulationsPage.getTotalElements(), simulationsPage.getTotalPages(), next,
                previous);

        return ResponseEntity.ok(
                new ApiResponseDto<List<Simulation>>(HttpStatus.OK.value(), "ok", simulationsPage.getContent(),
                        metadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getSimulationById(@Min(1) @RequestParam Long id) {
        log.info("Requesting simulation with id: {}", id);

        Simulation simulation = simulationService.findSimulationById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", simulation, null));
    }

    @PostMapping()
    public ResponseEntity<ApiResponseDto<?>> createSimulation(@Valid @RequestBody SimulationDto simulationDto) {
        log.info("Creating simulation");

        Simulation simulation = simulationService.addSimulation(simulationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(HttpStatus.CREATED.value(), "Simulation created successfully", simulation, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateSimulation(@Min(1) @RequestParam Long id,
            @Valid @RequestBody SimulationDto simulationDto) {
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
}