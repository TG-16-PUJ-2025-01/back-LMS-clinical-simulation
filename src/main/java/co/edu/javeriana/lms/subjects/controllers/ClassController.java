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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.subjects.dtos.ClassDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.services.ClassService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    @Autowired
    private AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter) {
        log.info("Requesting all classes");

        Page<ClassModel> classModelPage = classService.findAll(filter, page, size, sort, asc);

        log.info("Requesting all classes");

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<ClassModel>>(HttpStatus.OK.value(), "ok",
                classModelPage.getContent(), metadata));
    }

    @GetMapping("/{id}/member/all")
    public ResponseEntity<?> getAllClassMembers(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long id) {
        log.info("Requesting all members of the class");

        Page<User> classModelPage = classService.findAllMembers(filter, page, size, sort, asc, id, "");

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok",
                classModelPage.getContent(), metadata));
    }

    @GetMapping("/{id}/member/students")
    public ResponseEntity<?> getAllClassStudentsMembers(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long id) {
        log.info("Requesting all students members of the class");

        Page<User> classModelPage = classService.findAllMembers(filter, page, size, sort, asc, id,
                Role.ESTUDIANTE.name());

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok",
                classModelPage.getContent(), metadata));
    }

    @GetMapping("/{id}/member/professors")
    public ResponseEntity<?> getAllClassProfessorsMembers(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long id) {
        log.info("Requesting all professors members of the class");

        Page<User> classModelPage = classService.findAllMembers(filter, page, size, sort, asc, id,
                Role.PROFESOR.name());

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok",
                classModelPage.getContent(), metadata));
    }

    @GetMapping("/{id}/member/all/outside")
    public ResponseEntity<?> getAllClassMembersNotInClass(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long id) {
        log.info("Requesting all members out of the class");

        Page<User> classModelPage = classService.findAllNonMembers(filter, page, size, sort, asc, id, "");

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok",
                classModelPage.getContent(), metadata));
    }

    @GetMapping("/{id}/member/students/outside")
    public ResponseEntity<?> getAllClassStudentsMembersNotInClass(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long id) {
        log.info("Requesting all members out of the class");

        Page<User> classModelPage = classService.findAllNonMembers(filter, page, size, sort, asc, id,
                Role.ESTUDIANTE.name());

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok",
                classModelPage.getContent(), metadata));
    }

    @GetMapping("/{id}/member/professors/outside")
    public ResponseEntity<?> getAllClassProfessorsMembersNotInClass(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long id) {
        log.info("Requesting all professor members out of the class");

        Page<User> classModelPage = classService.findAllNonMembers(filter, page, size, sort, asc, id,
                Role.PROFESOR.name());

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, classModelPage.getNumberOfElements(),
                classModelPage.getTotalElements(),
                classModelPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponseDto<List<User>>(HttpStatus.OK.value(), "ok",
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

        classService.deleteById(id);

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                "Clase deleted successfully.", new ClassModel(), null));

    }

    @DeleteMapping("/delete/{idClass}/member/{idMember}")
    public ResponseEntity<?> deleteClassMemberById(@PathVariable Long idClass, @PathVariable Long idMember) {
        log.info("Deleting class memver with ID: " + idMember);

        ClassModel classModel = classService.findById(idClass);
        classModel.getProfessors().removeIf(user -> user.getId().equals(idMember));
        classModel.getStudents().removeIf(user -> user.getId().equals(idMember));

        classService.update(classModel);

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                "Member class deleted successfully.", classModel, null));

    }

    @Valid
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClass(@RequestBody ClassDto classModel, @PathVariable Long id) {
        log.info("Updating course with ID: " + id + " " + classModel.toString());

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                "Class updated successfully.", classService.update(classService.fromDtoToClass(classModel, id)), null));

    }

    @Valid
    @PutMapping("/update/{id}/members")
    public ResponseEntity<?> updateClassMembers(@RequestBody List<User> members, @PathVariable Long id) {
        log.info("Updating class members with ID: " + id);

        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                "Class updated successfully.", classService.updateMembers(members, id), null));

    }

    @Valid
    @PutMapping("/update/{id}/members/professor/{idProfessor}")
    public ResponseEntity<?> updateClassProfessorMember(@PathVariable Long id, @PathVariable Long idProfessor) {
        log.info("Updating class members with ID: " + id + " "+idProfessor);    
        
        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                    "Class updated successfully.", classService.updateMember(id,idProfessor,Role.PROFESOR), null));
        
    }

    @Valid
    @PutMapping("/update/{id}/members/student/{idStudent}")
    public ResponseEntity<?> updateClassStudentMember(@PathVariable Long id, @PathVariable Long idStudent) {
        log.info("Updating class members with ID: " + id + " "+idStudent);    
        return ResponseEntity.ok(new ApiResponseDto<ClassModel>(HttpStatus.OK.value(),
                    "Class updated successfully.", classService.updateMember(id,idStudent,Role.ESTUDIANTE), null));
        
    }


    @Valid
    @PostMapping("/add")
    public ResponseEntity<?> addClass(@Valid @RequestBody ClassDto classModel) {

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<ClassModel>(
                HttpStatus.CREATED.value(), "Class added successfully.", classService.save(classModel), null));
    }

    @GetMapping("/all/professor")
    public ResponseEntity<?> getClassesByProfesor(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer period,
            @RequestParam(defaultValue = "") String filter) {

        token = token.substring(7);
        log.info("Requesting main menu information for professor role");

        Long userId = authService.getUserIdByToken(token);
        log.info("User ID: " + userId);

        List<ClassModel> classes = classService.findByProfessorIdAndFilters(userId, year, period, filter);

        return ResponseEntity
                .ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Classes retrieved successfully", classes, null));
    }

    @GetMapping("/all/student")
    public ResponseEntity<?> getClassesByStudent(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer period,
            @RequestParam(defaultValue = "") String filter) {

        token = token.substring(7);
        log.info("Requesting main menu information for student role");

        Long userId = authService.getUserIdByToken(token);
        log.info("User ID: " + userId);

        List<ClassModel> classes = classService.findByStudentIdAndFilters(userId, year, period, filter);

        return ResponseEntity
                .ok(new ApiResponseDto<>(HttpStatus.OK.value(), "Classes retrieved successfully", classes, null));
    }
}
