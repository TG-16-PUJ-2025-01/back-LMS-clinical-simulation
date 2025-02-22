package co.edu.javeriana.lms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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
import co.edu.javeriana.lms.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.models.ClassModel;
import co.edu.javeriana.lms.services.ClassService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {

        log.info("Requesting all classes");

        String host = request.getHeader("Host");
        String scheme = request.getScheme();

        Page<ClassModel> classModelPage = classService.findAll(page, size);

        if (classModelPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No simulations found", null, null));
        }

        String previous = null;
        if (classModelPage.hasPrevious()) {
            previous = String.format("%s://%s/class/all?page=%d&size=%d", scheme, host, page - 1, size);
        }

        String next = null;
        if (classModelPage.hasNext()) {
            next = String.format("%s://%s/class/all?page=%d&size=%d", scheme, host, page + 1, size);
        }

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages(), next,
                previous);

        return ResponseEntity.ok(new ApiResponseDto<List<ClassModel>>(HttpStatus.OK.value(), "ok",
                classModelPage.getContent(), metadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@RequestParam Long id) {

        log.info("Requesting a class by id");

        ClassModel classModel = classService.findById(id);

        if (classModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No class found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(), "ok", classModel, null));

    }

    @DeleteMapping("/delete/{idClass}")
    public ResponseEntity<?> deleteClassById(@RequestParam Long id) {

        try {
            ClassModel classModel = classService.findById(id);
            classService.deleteById(id);
            return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                    "Clase deleted successfully.", classModel, null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.NOT_FOUND.value(), "Error: Class with ID " + id + " does not exist.", null, null));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponseDto<ClassModel>(HttpStatus.CONFLICT.value(),
                            "Error: Cannot delete the class because it has related data.", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error.", null, null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateClass(@RequestBody ClassModelDTO classModel) {
        try {
            return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                    "Class updated successfully.", classService.update(classModel), null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.NOT_FOUND.value(), "Error: " + e.getMessage(), null, null));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponseDto<ClassModel>(HttpStatus.BAD_REQUEST.value(), "Error: Invalid data.", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error.", null, null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClass(@RequestBody ClassModelDTO classModel) {

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.CREATED.value(), "Class added successfully.", classService.save(classModel), null));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.BAD_REQUEST.value(), "Error: Invalid data or duplicate entry.", null, null));
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.BAD_REQUEST.value(), "Error: Validation failed. " + e.getMessage(), null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<ClassModel>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error.", null, null));
        }
    }
}
