package co.edu.javeriana.lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.dtos.RegisterUserDto;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

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
        PaginationMetadataDto metadata = new PaginationMetadataDto(page, users.getNumberOfElements(), users.getTotalElements(), users.getTotalPages() , null, null);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Users retrieved successfully", users.getContent(), metadata));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody RegisterUserDto registerUserDTO) {
        User user = userService.addUser(registerUserDTO.toUser());
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.CREATED.value(), "User created successfully", user, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "User retrieved successfully", user, null));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody RegisterUserDto registerUserDTO) {
        User user = userService.updateUserById(id, registerUserDTO.toUser());
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "User updated successfully", user, null));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserById (@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "User deleted successfully", null, null));
    }
}
