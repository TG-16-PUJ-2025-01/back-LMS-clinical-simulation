package co.edu.javeriana.lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.services.RoomService;

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
    public ResponseEntity<?> getAllRooms() {
        return ResponseEntity.ok(roomService.findAll());
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
    public ResponseEntity<?> addRoom(@RequestBody Room room) {
        return ResponseEntity.ok(roomService.save(room));
    }
    
    
}
