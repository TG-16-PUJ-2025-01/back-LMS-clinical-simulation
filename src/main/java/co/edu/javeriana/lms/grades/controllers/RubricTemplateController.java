package co.edu.javeriana.lms.grades.controllers;

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

import co.edu.javeriana.lms.grades.dtos.RubricTemplateDTO;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.grades.services.RubricTemplateService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.subjects.dtos.CourseDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.services.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/rubric/template")
public class RubricTemplateController {

    
    @Autowired
    private RubricTemplateService rubricTemplateService;

    //list the rubrics
    //solo listar los archived si eres coordinador, admin o el dueño de la rubrica
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

        Page<RubricTemplate> rubricsTemplatesPage = rubricTemplateService.findAll(filter, page, size, sort, asc);

        String previous = null;
        if (rubricsTemplatesPage.hasPrevious()) {
            previous = String.format("%s://%s/course/all?page=%d&size=%d", scheme, host, page - 1, size);
        }

        String next = null;
        if (rubricsTemplatesPage.hasNext()) {
            next = String.format("%s://%s/course/all?page=%d&size=%d", scheme, host, page + 1, size);
        }

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, rubricsTemplatesPage.getNumberOfElements(),
                rubricsTemplatesPage.getTotalElements(),
                rubricsTemplatesPage.getTotalPages(), next,
                previous);

        return ResponseEntity.ok(new ApiResponseDto<List<RubricTemplate>>(HttpStatus.OK.value(), "ok", rubricsTemplatesPage.getContent(), metadata));
    }

    //get the rubric template by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getRubricTemplateById(@PathVariable Long id) {
        log.info("Requesting rubric tamplate with ID: " + id);

        RubricTemplate rubricTemplate = rubricTemplateService.findById(id);

        if (rubricTemplate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No rubric template found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(), "ok", rubricTemplate, null));
    }


    //create the rubric template
    @Valid
    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@Valid @RequestBody RubricTemplateDTO rubricTemplate) {
        log.info("Adding a rubric template");

        RubricTemplate newRubricTemplate = rubricTemplateService.save(rubricTemplate);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(),
                "Class added successfully.", newRubricTemplate, null));
    }
    

    //update the rubric template
    //this depends on several factors, like if the rubric template is being used by a rubric, if it is being used by a course, etc.
    //if the rubric template is being used by a rubric, then the rubric template cannot be updated, we will need to create a new rubric template
    //if it is no been used by a rubric, then we can update the rubric template 
    //if we are updating the rubric of the practice, then we need to update the rubric of the practice (vearing in mind that the ruric may be used by other people)


    
    @PutMapping("/archive/{id}")
    public ResponseEntity<?> archivelassById(@PathVariable Long id) {
        log.info("Archiving rubric template with ID: " + id);
        
        RubricTemplate rubricTemplate=rubricTemplateService.archiveById(id);

        return ResponseEntity.ok(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(),
                "Clase deleted successfully.", rubricTemplate, null));
        
    }

    //delete the rubric template

}
