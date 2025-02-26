package co.edu.javeriana.lms.controllers;

import java.util.List;

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

import co.edu.javeriana.lms.dtos.ClassDto;
import co.edu.javeriana.lms.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.models.ClassModel;
import co.edu.javeriana.lms.services.ClassService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
            @Min(1) @RequestParam(defaultValue = "10") Integer size, 
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            HttpServletRequest request) {

        log.info("Requesting all classes");

        String host = request.getHeader("Host");
        String scheme = request.getScheme();

        Page<ClassModel> classModelPage = classService.findAll(filter, page, size, sort, asc);

        log.info("Requesting all classes");

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
    public ResponseEntity<?> getClassById(@PathVariable Long id) {

        log.info("Requesting a class by id");

        ClassModel classModel = classService.findById(id);

        if (classModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No class found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(), "ok", classModel, null));

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClassById(@PathVariable Long id) {
        log.info("Deleting course with ID: " + id);

        ClassModel classModel = classService.findById(id);
        classService.deleteById(id);

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                "Clase deleted successfully.", classModel, null));
        
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClass(@RequestBody ClassDto classModel, @PathVariable Long id) {
        log.info("Updating course with ID: " + id);

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                    "Class updated successfully.", classService.update(classModel, id), null));
        
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClass(@Valid @RequestBody ClassDto classModel) {
       
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<ClassModel>(
                HttpStatus.CREATED.value(), "Class added successfully.", classService.save(classModel), null));
    }
}
