package co.edu.javeriana.lms.practices.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.services.SimulationService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
@RequestMapping(value = "/simulation")
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllSimulations(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.info("Requesting all simulations");

        Page<Simulation> simulationsPage = simulationService.getAllSimulations(page, size);

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

}