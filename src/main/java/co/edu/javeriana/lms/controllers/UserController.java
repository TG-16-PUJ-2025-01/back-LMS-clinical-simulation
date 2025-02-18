package co.edu.javeriana.lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.RegisterUserDto;
import co.edu.javeriana.lms.dtos.ResponseUserDto;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ResponseUserDto> createUser(@Valid @RequestBody RegisterUserDto registerUserDTO) {
        User user = userService.createUser(registerUserDTO.toUser());
        ResponseUserDto responseUserDTO = new ResponseUserDto();
        responseUserDTO.userToResponseUserDTO(user);
        return ResponseEntity.ok(responseUserDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserByEmail(@Valid @RequestBody String email) {
        userService.deleteByEmail(email);
        return ResponseEntity.ok("User deleted");
    }
}
