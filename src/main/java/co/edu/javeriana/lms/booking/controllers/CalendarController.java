package co.edu.javeriana.lms.booking.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.booking.dtos.EventDto;
import co.edu.javeriana.lms.booking.services.CalendarService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CalendarService calendarService;

    @GetMapping("")
    public ResponseEntity<?> getEvents(@RequestHeader("Authorization") String token, @RequestParam String start, @RequestParam String end) {
        token = token.substring(7);
        log.info("Requesting events for user with token: {}", token);

        Long userId = authService.getUserIdByToken(token);

        List<EventDto> realEvents = calendarService.searchEvents(userId, start, end);

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Events retrieved successfully", realEvents, null));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEvents(@RequestHeader("Authorization") String token, @RequestParam String start, @RequestParam String end) {
        token = token.substring(7);
        log.info("Requesting all events for admin user with token: {}", token);

        Long userId = authService.getUserIdByToken(token);

        List<EventDto> realEvents = calendarService.searchAllEvents(userId, start, end);

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Events retrieved successfully", realEvents, null));
    }
}
