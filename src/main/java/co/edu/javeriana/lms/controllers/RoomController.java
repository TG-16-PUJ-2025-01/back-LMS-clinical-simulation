package co.edu.javeriana.lms.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.RoomDto;
import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.models.RoomType;
import co.edu.javeriana.lms.services.RoomService;
import co.edu.javeriana.lms.services.RoomTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeService roomTypeService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllRooms(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Room> roomsPage = roomService.findAll(pageable);

        if (roomsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No rooms found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Rooms found", roomsPage, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomService.findById(id);

        if (room.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Room not found", null, null));
        }
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>(HttpStatus.OK.value(), "Room found", roomService.findById(id), null));
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponseDto<?>> getRoomTypes() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>(HttpStatus.OK.value(), "Room types found", roomService.findAllTypes(), null));
    }

    @DeleteMapping("/delete/{idRoom}")
    public ResponseEntity<ApiResponseDto<?>> deleteRoomById(@PathVariable("idRoom") Long id) {

        // Check if room exists
        Optional<Room> room = roomService.findById(id);

        if (room.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Room not found", null, null));
        }

        roomService.deleteById(id);
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>(HttpStatus.OK.value(), "Room deleted successfully", null, null));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseDto<?>> updateRoom(@Valid @RequestBody Room room) {

        // Check if room exists
        Optional<Room> roomEntity = roomService.findById(room.getId());

        if (roomEntity.isEmpty()) {
            log.error("Room not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Room not found", null, null));
        }

        // Update room using the save method
        try {
            Room updatedRoom = roomService.update(room);
            log.info("Updated room: {}", updatedRoom);

            // Return updated room
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDto<>(HttpStatus.OK.value(), "Room updated successfully", updatedRoom, null));
        } catch (IllegalArgumentException e) {
            // If there's an error (like room name conflict)
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDto<>(HttpStatus.CONFLICT.value(), e.getMessage(), null, null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponseDto<?>> addRoom(@Valid @RequestBody RoomDto roomDto) {

        Room roomEntity = roomDto.toEntity(null);
        Room savedRoom = roomService.save(roomEntity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(HttpStatus.CREATED.value(), "Room created successfully", savedRoom, null));
    }

    @PostMapping("/type/add")
    public ResponseEntity<ApiResponseDto<?>> addRoomType(@Valid @RequestBody RoomType type) {

        RoomType savedType = roomTypeService.save(type);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(HttpStatus.CREATED.value(), "Room type created successfully", savedType, null));
    }
    
    
}
