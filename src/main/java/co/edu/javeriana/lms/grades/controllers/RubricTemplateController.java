package co.edu.javeriana.lms.grades.controllers;

import java.security.Principal;
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
import co.edu.javeriana.lms.shared.dtos.ErrorDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.subjects.models.Course;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/rubric/template")
public class RubricTemplateController {

    @Autowired
    private RubricTemplateService rubricTemplateService;

    // list the rubrics
    // solo listar los archived si eres admin o el dueño de la rubrica
    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "rubricTemplateId") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "true") Boolean mine,
            @RequestParam(defaultValue = "true") Boolean archived,
            Principal principal) {
        log.info("Requesting all courses");

        Page<RubricTemplate> rubricsTemplatesPage = rubricTemplateService.findAll(filter, page, size, sort, asc, mine,
                archived, principal.getName());

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, rubricsTemplatesPage.getNumberOfElements(),
                rubricsTemplatesPage.getTotalElements(),
                rubricsTemplatesPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<RubricTemplate>>(HttpStatus.OK.value(), "ok",
                rubricsTemplatesPage.getContent(), metadata));
    }

    // get the rubric template by id
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

    // create the rubric template
    @Valid
    @PostMapping
    public ResponseEntity<?> addRubricTemplate(@Valid @RequestBody RubricTemplateDTO rubricTemplate,
            Principal principal) {
        log.info("Adding a rubric template");

        RubricTemplate newRubricTemplate = rubricTemplateService.save(rubricTemplate, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(),
                "Rubric template added successfully.", newRubricTemplate, null));
    }

    // update the rubric template
    @Valid
    @PutMapping("/{id}")
    public ResponseEntity<?> updatRubricTemplate(@RequestBody RubricTemplateDTO rubricTemplate, @PathVariable Long id) {
        log.info("Updating rubric template with ID: " + id);

        RubricTemplate rubricTemplateUpdated = rubricTemplateService.update(rubricTemplate, id);

        return ResponseEntity.ok(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(),
                "Course updated successfully.", rubricTemplateUpdated, null));
    }

    // change the status of the rubric template to archived
    @PutMapping("/archive/{id}")
    public ResponseEntity<?> archivelassById(@PathVariable Long id) {
        log.info("Archiving rubric template with ID: " + id);

        RubricTemplate rubricTemplate = rubricTemplateService.archiveById(id);

        return ResponseEntity.ok(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(),
                "Clase deleted successfully.", rubricTemplate, null));

    }

    @PutMapping("/unarchive/{id}")
    public ResponseEntity<?> unarchivelassById(@PathVariable Long id) {
        log.info("Archiving rubric template with ID: " + id);

        RubricTemplate rubricTemplate = rubricTemplateService.unarchiveById(id);

        return ResponseEntity.ok(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(),
                "Clase deleted successfully.", rubricTemplate, null));

    }

    // delete the rubric template
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRubricTemplateById(@PathVariable Long id) {

        log.info("Deleting rubric template with ID: " + id);

        try {
            // RubricTemplate rubricTemplate = rubricTemplateService.findById(id);
            rubricTemplateService.deleteById(id);

            return ResponseEntity.ok(new ApiResponseDto<>(
                    HttpStatus.OK.value(),
                    "Rubric Template deleted successfully.",
                    new RubricTemplate(),
                    null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponseDto<ErrorDto>(HttpStatus.BAD_REQUEST.value(),
                            "Unable to delete rubric template as other courses or practices are using it", null, null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponseDto<ErrorDto>(HttpStatus.NOT_FOUND.value(),
                            "rubric template with id " + id + " not found", null, null));
        }
    }

    // add new courses to the rubric template if the rubric is only used and not
    // edited
    @Valid
    @PutMapping("/{id}/courses")
    public ResponseEntity<?> updatRubricCourses(@RequestBody List<Course> courses, @PathVariable Long id) {

        log.info("Updating rubric template with ID: " + id);

        RubricTemplate rubricTemplateUpdated = rubricTemplateService.updateRubricTemplateCourses(courses, id);

        return ResponseEntity.ok(new ApiResponseDto<RubricTemplate>(HttpStatus.OK.value(),
                "Course updated successfully.", rubricTemplateUpdated, null));
    }

    // return the suggested rubrics if the practice is part of a chosen course
    @GetMapping("/recommended/{idPractice}")
    public ResponseEntity<?> getRecommendedRubricTemplatesByCoursesById(@PathVariable Long idPractice) {
        log.info("Requesting suggested rubric templates with practice ID: " + idPractice);

        List<RubricTemplate> rubricTemplates = rubricTemplateService
                .findRecommendedRubricTemplatesByCoursesById(idPractice);

        if (rubricTemplates.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No rubric template found", null, null));
        }

        return ResponseEntity
                .ok(new ApiResponseDto<List<RubricTemplate>>(HttpStatus.OK.value(), "ok", rubricTemplates, null));
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getRubricCourses(@PathVariable Long id) {
        log.info("Requesting rubric courses with rubric ID: " + id);

        RubricTemplate rubricTemplate = rubricTemplateService.findById(id);

        if (rubricTemplate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "No rubric template found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<List<Course>>(HttpStatus.OK.value(), "ok", rubricTemplate.getCourses(),
                null));
    }

}
