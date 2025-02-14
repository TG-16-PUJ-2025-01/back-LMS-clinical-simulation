package co.edu.javeriana.lms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.ClassModelDTO;
import co.edu.javeriana.lms.services.ClassService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/class")
public class ClassModelController {
    
    @Autowired
    private  ClassService classService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
        @Min(0) @RequestParam(defaultValue = "0") Integer page,
        @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("Requesting all classes");
        
        List<ClassModelDTO> classes = classService.findAll(page, size);

        if (classes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No simulations found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<List<ClassModelDTO>>(HttpStatus.OK.value(), "ok", classes, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@RequestParam Long id) {
        
         
        log.info("Requesting a class by id");

        ClassModelDTO classModel=classService.findById(id);

        if(classModel==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No class found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<ClassModelDTO>(HttpStatus.OK.value(), "ok", classModel, null));

    }


    @DeleteMapping("/delete/{idClass}")
    public ResponseEntity<?> deleteClassById(@RequestParam Long id) {
        
        try {
            classService.deleteById(id);
            return ResponseEntity.ok().body("Clase deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Class with ID " + id + " does not exist.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Cannot delete the class because it has related data.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateClass(@RequestBody ClassModelDTO classModel) {
        try {
            classService.update(classModel);
            return ResponseEntity.ok().body("Class updated successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid data.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }
    

    @PostMapping("/add")
    public ResponseEntity<?> addClass(@RequestBody ClassModelDTO classModel) {

        try {
            classService.save(classModel);
            return ResponseEntity.status(HttpStatus.CREATED).body("Class added successfully.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid data or duplicate entry.");
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Validation failed. " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }
}
