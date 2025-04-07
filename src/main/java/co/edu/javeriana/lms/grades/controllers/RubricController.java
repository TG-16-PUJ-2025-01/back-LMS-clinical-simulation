package co.edu.javeriana.lms.grades.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.grades.dtos.RubricDto;
import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.grades.services.RubricService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/rubric")
public class RubricController {

    @Autowired
    private RubricService rubricService;
    
    //editar rubrica
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRubricCriteria(@Valid @RequestBody RubricDto rubricDto, @PathVariable Long id) {
        log.info("Updating rubric with ID: " + id);

        Rubric rubricUpdated = rubricService.update(rubricDto, id);

        return ResponseEntity.ok(new ApiResponseDto<Rubric>(HttpStatus.OK.value(),
                "Rubric updated successfully.", rubricUpdated, null));
    }

    @PostMapping("/")
    public ResponseEntity<?> createRubric(@Valid @RequestBody RubricDto rubricDto) {
        log.info("Creating rubric");

        Rubric rubricCreated = rubricService.create(rubricDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<Rubric>(HttpStatus.CREATED.value(), "Rubric created successfully.", rubricCreated, null));
    }

    //ver rubrica
    @GetMapping("/{id}")
    public ResponseEntity<?> getRubricById(@PathVariable Long id) {
        log.info("Requesting rubric with ID: " + id);

        Rubric rubric = rubricService.findById(id);

        if (rubric == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No rubric found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<Rubric>(HttpStatus.OK.value(), "ok", rubric, null));
    } 
}
