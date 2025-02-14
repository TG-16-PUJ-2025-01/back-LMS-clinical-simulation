package co.edu.javeriana.lms.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import co.edu.javeriana.lms.dtos.CourseDTO;
import co.edu.javeriana.lms.models.ClassModel;
import co.edu.javeriana.lms.services.CourseService;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/course")
public class CourseController {
    
    @Autowired
    private  CourseService courseService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@Min(0) @RequestParam(defaultValue = "0") Integer page,
    @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("Requesting all classes");
        
        List<CourseDTO> courses = courseService.findAll(page, size);

        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No simulations found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<List<CourseDTO>>(HttpStatus.OK.value(), "ok", courses, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@RequestParam Long id) {
        
        log.info("Requesting a class by id");

        CourseDTO course=courseService.findById(id);

        if(course==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No class found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<CourseDTO>(HttpStatus.OK.value(), "ok", course, null));

    }

    @DeleteMapping("/delete/{idRoom}")
    public ResponseEntity<?> deleteCourseById(@RequestParam Long id) {
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatCourse(@RequestBody ClassModel classModel) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestBody ClassModel classModel) {

        return ResponseEntity.ok().build();
    }
}
