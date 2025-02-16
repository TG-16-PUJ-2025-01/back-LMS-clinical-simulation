package co.edu.javeriana.lms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.RegisterUserDTO;
import co.edu.javeriana.lms.dtos.ResponseUserDTO;
import co.edu.javeriana.lms.dtos.UserListDTO;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ResponseUserDTO> createUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        User user = userService.createUser(registerUserDTO.toUser());
        ResponseUserDTO responseUserDTO = new ResponseUserDTO();
        responseUserDTO.userToResponseUserDTO(user);
        return ResponseEntity.ok(responseUserDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserByEmail(@Valid @RequestBody String email) {
        userService.deleteByEmail(email);
        return ResponseEntity.ok("User deleted");
    }

    @GetMapping("/all/coordinator")
    public ResponseEntity<?> getAllCoordinators() {
        
        log.info("Requesting all classes");
        
        List<User> coordinators = userService.findAllCoordinators();

        if (coordinators.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No simulations found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok", coordinators, null));
    }

    @GetMapping("/all/professor")
    public ResponseEntity<?> getAllProfessors() {
        
        log.info("Requesting all professors");
        
        List<User> professors = userService.findAllProfessors();

        if (professors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No professors found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok", professors, null));
    }
}
