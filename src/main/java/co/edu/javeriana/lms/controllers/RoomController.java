package co.edu.javeriana.lms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.RoomDto;
import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.services.RoomService;
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
    public ResponseEntity<?> getRoomById(@RequestParam Long id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    @DeleteMapping("/delete/{idRoom}")
    public ResponseEntity<?> deleteRoomById(@RequestParam Long id) {
        roomService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRoom(@RequestBody Room room) {
        return ResponseEntity.ok(roomService.save(room));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRoom(@RequestBody RoomDto room) {

        // RoomMapper mapper = new RoomMapper();
        // Room roomEntity = mapper.toEntity(room);


        // return ResponseEntity.ok(roomService.save(roomEntity));

        return null;
    }
    
    
}
