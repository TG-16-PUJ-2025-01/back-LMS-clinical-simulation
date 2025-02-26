package co.edu.javeriana.lms.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.services.SimulationService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import jakarta.servlet.http.HttpServletRequest;
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
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Requesting all simulations");

        String host = request.getHeader("Host");
        String scheme = request.getScheme();

        Page<Simulation> simulationsPage = simulationService.getAllSimulations(page, size);

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

}