package co.edu.javeriana.lms.practices.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.practices.dtos.PracticeDto;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.services.PracticeService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@RestController
@RequestMapping(value = "/practice")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllPractices(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter) {
        log.info("Requesting all practices");

        Page<Practice> practicesPage = practiceService.findAll(filter, page, size, sort, asc);

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, practicesPage.getSize(),
                practicesPage.getTotalElements(), practicesPage.getTotalPages());

        return ResponseEntity
                .ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", practicesPage.getContent(), metadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getPracticeById(@PathVariable Long id) {
        log.info("Requesting practice with id: {}", id);

        Practice practice = practiceService.findById(id);

        return ResponseEntity
                .ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Practice retrieved successfully", practice, null));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponseDto<?>> getPracticesByClassId(@PathVariable Long classId) {
        log.info("Requesting practices by class with id: {}", classId);

        List<Practice> practices = practiceService.findByClassId(classId);

        return ResponseEntity
                .ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Practices retrieved successfully", practices, null));
    }

    @PostMapping("/add/{classId}")
    public ResponseEntity<ApiResponseDto<?>> addPractice(@PathVariable Long classId,
            @Valid @RequestBody PracticeDto practiceDto) {
        log.info("Adding practice to class with id: {}", classId);

        Practice newPractice = practiceService.save(classId, practiceDto.toEntity());

        return ResponseEntity.ok(
                new ApiResponseDto<>(HttpStatus.CREATED.value(), "Practice created successfully", newPractice, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updatePractice(@PathVariable Long id,
            @Valid @RequestBody PracticeDto practiceDto) {
        log.info("Updating practice with id: {}", id);

        Practice updatedPractice = practiceService.update(id, practiceDto.toEntity());

        return ResponseEntity.ok(
                new ApiResponseDto<>(HttpStatus.OK.value(), "Practice updated successfully", updatedPractice, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deletePracticeById(@PathVariable Long id) {
        log.info("Deleting practice with id: {}", id);

        practiceService.deleteById(id);

        return ResponseEntity
                .ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Practice deleted successfully", null, null));
    }

    @PutMapping("/{id}/rubric/{rubricId}")
    public ResponseEntity<ApiResponseDto<?>> updatePracticeRubric(@PathVariable Long id, @PathVariable Long rubricId) {
        log.info("Updating rubric for practice with id: {}", id);

        Practice updatedPractice = practiceService.updateRubric(id, rubricId);

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Practice rubric updated successfully",
                updatedPractice, null));
    }

    @GetMapping("/{id}/enrolled")
    public ResponseEntity<ApiResponseDto<?>> getEnrolledSimulation(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        log.info("Requesting enrolled simulation with id: {}", id);

        token = token.substring(7);

        Long userId = authService.getUserIdByToken(token);

        Long simulationId = practiceService.getEnroledSimulation(id, userId);

        return ResponseEntity.ok(new ApiResponseDto<>(HttpStatus.OK.value(), "ok", simulationId, null));
    }

}
