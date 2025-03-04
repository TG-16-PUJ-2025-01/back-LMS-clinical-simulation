package co.edu.javeriana.lms.subjects.controllers;

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

import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.subjects.dtos.CourseDto;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.services.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "courseId") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            HttpServletRequest request) {
        log.info("Requesting all courses");

        String host = request.getHeader("Host");
        String scheme = request.getScheme();

        Page<Course> coursesPage = courseService.findAll(filter, page, size, sort, asc);

        String previous = null;
        if (coursesPage.hasPrevious()) {
            previous = String.format("%s://%s/course/all?page=%d&size=%d", scheme, host, page - 1, size);
        }

        String next = null;
        if (coursesPage.hasNext()) {
            next = String.format("%s://%s/course/all?page=%d&size=%d", scheme, host, page + 1, size);
        }

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, coursesPage.getNumberOfElements(),
                coursesPage.getTotalElements(),
                coursesPage.getTotalPages(), next,
                previous);

        return ResponseEntity
                .ok(new ApiResponseDto<List<Course>>(HttpStatus.OK.value(), "ok", coursesPage.getContent(), metadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        log.info("Requesting course with ID: " + id);

        Course course = courseService.findById(id);

        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No class found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<Course>(HttpStatus.OK.value(), "ok", course, null));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourseById(@PathVariable Long id) {
        log.info("Deleting course with ID: " + id);

        Course course = courseService.findById(id);
        courseService.deleteById(id);

        return ResponseEntity.ok(new ApiResponseDto<Course>(HttpStatus.OK.value(),
                "Course deleted successfully.", course, null));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatCourse(@RequestBody CourseDto courseModel, @PathVariable Long id) {
        log.info("Updating course with ID: " + id);

        Course course = courseService.update(courseModel, id);

        return ResponseEntity.ok(new ApiResponseDto<Course>(HttpStatus.OK.value(),
                "Course updated successfully.", course, null));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@Valid @RequestBody CourseDto courseModel) {
        log.info("Adding a course");

        Course course = courseService.save(courseModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<Course>(HttpStatus.OK.value(),
                "Class added successfully.", course, null));
    }
}
