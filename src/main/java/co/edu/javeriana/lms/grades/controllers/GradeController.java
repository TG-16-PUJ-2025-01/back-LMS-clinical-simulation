package co.edu.javeriana.lms.grades.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.grades.services.GradeService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/grade")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @GetMapping("class/{classId}")
    public ResponseEntity<?> getClassGrades(@PathVariable Long classId) {
        log.info("Requesting grades for class with ID: " + classId);
        return ResponseEntity.ok(new ApiResponseDto<>(
                200,
                "ok",
                gradeService.getFinalGradesByClass(classId),
                null));
    }
}
