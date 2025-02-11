package co.edu.javeriana.lms.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.RoomDto;
import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.services.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllRooms(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {

        List<Room> rooms = roomService.findAll(page, size);

        if (rooms.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No rooms found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Rooms found", rooms, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getRoomById(@RequestParam Long id) {

        Optional<Room> room = roomService.findById(id);

        if (room.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Room not found", null, null));
        }
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>(HttpStatus.OK.value(), "Room found", roomService.findById(id), null));
    }

    @DeleteMapping("/delete/{idRoom}")
    public ResponseEntity<ApiResponseDto<?>> deleteRoomById(@RequestParam Long id) {

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
    public ResponseEntity<ApiResponseDto<?>> updateRoom(@RequestBody RoomDto roomDto) {

        // TODO: Fix this method
        
        // Search for the room name in the database
        Room existingRoom = roomService.findByName(roomDto.getName());

        // If the room does not exist, return error
        if (existingRoom == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Room not found", null, null));
        }

        // If the name changed, check for duplicates before saving
        if (!existingRoom.getName().equals(roomDto.getName()) && roomService.existsByName(roomDto.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDto<>(HttpStatus.CONFLICT.value(), "Room name already exists", null, null));
        }

        // Update the fields
        existingRoom.setName(roomDto.getName());
        existingRoom.setType(roomDto.getType().toEntity());

        // Save the room
        Room updatedRoom = roomService.save(existingRoom);

        // Return the updated room
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>(HttpStatus.OK.value(), "Room updated successfully", updatedRoom, null));
    }


    @PostMapping("/add")
    public ResponseEntity<ApiResponseDto<?>> addRoom(@Valid @RequestBody RoomDto roomDto) {

        Room roomEntity = roomDto.toEntity(null);
        Room savedRoom = roomService.save(roomEntity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(HttpStatus.CREATED.value(), "Room created successfully", savedRoom, null));
    }
    
    
}
