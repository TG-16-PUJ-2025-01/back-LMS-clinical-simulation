package co.edu.javeriana.lms.grades.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.grades.dtos.StudentGradeDto;
import co.edu.javeriana.lms.grades.dtos.PracticePercentageDto;
import co.edu.javeriana.lms.grades.dtos.PracticesPercentagesDto;
import co.edu.javeriana.lms.grades.services.GradeService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/grade")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private AuthService authService;

    @GetMapping("class/{classId}")
    public ResponseEntity<?> getClassGrades(@PathVariable Long classId) {
        log.info("Requesting grades for class with ID: " + classId);
        return ResponseEntity.ok(new ApiResponseDto<>(
                200,
                "ok",
                gradeService.getFinalGradesByClass(classId),
                null));
    }

    @GetMapping("/student/{classId}")
    public ResponseEntity<?> getStudentGradeByToken(@RequestHeader("Authorization") String token, @PathVariable Long classId) {

        token = token.substring(7);
        log.info("Requesting student grades for class with ID: " + classId + " and token: " + token);

        Long userId = authService.getUserIdByToken(token);

        StudentGradeDto studentGrades = gradeService.getGradesByUserAndClass(classId, userId);

        return ResponseEntity.ok(new ApiResponseDto<>(
                200,
                "ok",
                studentGrades,
                null));
    }

    @PutMapping("/percentages")
    public ResponseEntity<?> updateClassGradePercentages(
            @RequestBody PracticesPercentagesDto classGradePercentagesDto) {
        log.info("Requesting to update grade percentages");
        gradeService.updateClassGradePercentages(classGradePercentagesDto);
        return ResponseEntity.ok(new ApiResponseDto<>(
                200,
                "ok",
                null,
                null));
    }

    @GetMapping("/class/{classId}/percentages")
    public ResponseEntity<?> getPracticesPercentagesByClass(@PathVariable Long classId) {
        log.info("Requesting grade percentages for class with ID: " + classId);
        List<PracticePercentageDto> classGradePercentagesDto = gradeService.getPracticesPercentagesByClass(classId);
        return ResponseEntity.ok(new ApiResponseDto<>(
                200,
                "ok",
                classGradePercentagesDto,
                null));
    }
}
