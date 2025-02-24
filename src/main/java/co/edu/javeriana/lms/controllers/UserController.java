package co.edu.javeriana.lms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.ApiResponseDtos;
import co.edu.javeriana.lms.dtos.PaginationMetadataDtos;
import co.edu.javeriana.lms.dtos.RegisterUserDtos;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "courseId") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            HttpServletRequest request
    ) {
        Page<User> users = userService.getAllUsers(filter, page, size, sort, asc);
        PaginationMetadataDtos metadata = new PaginationMetadataDtos(page, users.getNumberOfElements(), users.getTotalElements(), users.getTotalPages() , null, null);
        return ResponseEntity.ok(new ApiResponseDtos<>(HttpStatus.OK.value(), "Users retrieved successfully", users.getContent(), metadata));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody RegisterUserDtos registerUserDTO) {
        User user = userService.addUser(registerUserDTO.toUser());
        return ResponseEntity.ok(new ApiResponseDtos<>(HttpStatus.CREATED.value(), "User created successfully", user, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponseDtos<>(HttpStatus.OK.value(), "User retrieved successfully", user, null));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody RegisterUserDtos registerUserDTO) {
        User user = userService.updateUserById(id, registerUserDTO.toUser());
        return ResponseEntity.ok(new ApiResponseDtos<>(HttpStatus.OK.value(), "User updated successfully", user, null));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserById (@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(new ApiResponseDtos<>(HttpStatus.OK.value(), "User deleted successfully", null, null));
    }


    @GetMapping("/all/coordinator")
    public ResponseEntity<?> getAllCoordinators() {
        
        log.info("Requesting all classes");
        
        List<User> coordinators = userService.findAllCoordinators();

        if (coordinators.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDtos<>(HttpStatus.NOT_FOUND.value(), "No simulations found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDtos<List<User>>(HttpStatus.OK.value(), "ok", coordinators, null));
    }

    @GetMapping("/all/professor")
    public ResponseEntity<?> getAllProfessors() {
        
        log.info("Requesting all professors");
        
        List<User> professors = userService.findAllProfessors();

        if (professors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDtos<>(HttpStatus.NOT_FOUND.value(), "No professors found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDtos<List<User>>(HttpStatus.OK.value(), "ok", professors, null));
    }
}
