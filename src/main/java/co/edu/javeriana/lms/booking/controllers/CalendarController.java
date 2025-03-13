package co.edu.javeriana.lms.booking.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.booking.dtos.EventDto; // Crea esta clase para representar un evento

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @GetMapping("")
    public ResponseEntity<?> getEvents(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        log.info("Requesting events for user with token: {}", token);

        // Simulación de eventos
        List<EventDto> events = List.of(
            new EventDto(1, "Reunión", "2025-03-12 09:00", "2025-03-12 16:00"),
            new EventDto(2, "Entrega", "2025-03-15", "2025-03-15")
        );


        token = token.substring(7);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Roles retrieved successfully", events, null));
    }
}

