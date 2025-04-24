package co.edu.javeriana.lms.grades.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.grades.dtos.PracticePercentageDto;
import co.edu.javeriana.lms.grades.dtos.PracticesPercentagesDto;
import co.edu.javeriana.lms.grades.dtos.StudentGradeDto;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GradeService {
    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<StudentGradeDto> getFinalGradesByClass(Long classModelId) {
        log.info("Fetching final grades for class with ID: {}", classModelId);

        ClassModel classModel = classRepository.findById(classModelId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        List<Simulation> simulations = simulationRepository.findAllByPractice_ClassModel(classModel);

        // Paso 1: Obtener prácticas calificables únicas
        Map<String, Practice> gradeablePractices = new HashMap<>();
        for (Practice practice : classModel.getPractices()) {
            if (Boolean.TRUE.equals(practice.getGradeable())) {
                gradeablePractices.putIfAbsent(practice.getName(), practice);
            }
        }

        // Paso 2: Map de estudianteId -> DTO con notas
        Map<String, StudentGradeDto> studentGradesMap = new HashMap<>();

        for (User student : classModel.getStudents()) {
            String studentName = student.getLastName() + " " + student.getName();
            studentGradesMap.putIfAbsent(studentName, new StudentGradeDto(studentName));
        }

        // Paso 3: Procesar simulaciones con nota
        for (Simulation simulation : simulations) {
            Practice practice = simulation.getPractice();
            if (!Boolean.TRUE.equals(practice.getGradeable()))
                continue;

            Float grade = simulation.getGrade();
            if (grade == null)
                continue;

            Float gradePercentage = practice.getGradePercentage();
            if (gradePercentage == null)
                gradePercentage = 0f;

            for (User student : simulation.getUsers()) {
                String studentName = student.getLastName() + " " + student.getName();
                studentGradesMap.putIfAbsent(studentName, new StudentGradeDto(studentName));

                StudentGradeDto dto = studentGradesMap.get(studentName);

                // Si ya existe una nota para esta práctica, escoger la más alta
                Float currentGrade = dto.getPracticeGrades().getOrDefault(practice.getName(), null);
                Float newGrade = (currentGrade == null) ? grade : Math.max(currentGrade, grade);
                dto.addPracticeGrade(practice.getName(), newGrade);

                // Nota ponderada acumulada
                Float previousFinal = dto.getFinalGrade();
                Float updatedFinal = previousFinal + (grade * gradePercentage / 100f);
                dto.setFinalGrade(updatedFinal);
            }
        }

        // Paso 4: Asegurar que cada estudiante tenga una entrada para todas las
        // prácticas
        for (StudentGradeDto dto : studentGradesMap.values()) {
            for (String practiceName : gradeablePractices.keySet()) {
                dto.getPracticeGrades().putIfAbsent(practiceName, null);
            }
        }

        // Paso 5: Ordenar las notas de las prácticas por nombre de práctica
        for (StudentGradeDto dto : studentGradesMap.values()) {

            dto.setPracticeGrades(dto.getPracticeGrades().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // más limpio
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll));

        }

        return new ArrayList<>(studentGradesMap.values());
    }

    public StudentGradeDto getGradesByUserAndClass(Long classId, Long userId) {
        // Search for the class by id
        ClassModel classModel = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // Search for the user by id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Search for practices of the class
        List<Practice> practices = classModel.getPractices();

        // Search for the simulation of the user in the class, extract the grade from
        // there
        List<Simulation> simulations = simulationRepository.findAllByPractice_ClassModel(classModel);
        List<Simulation> userSimulations = new ArrayList<>();
        for (Simulation simulation : simulations) {
            if (simulation.getUsers().contains(user)) {
                userSimulations.add(simulation);
            }
        }

        // Create a StudentGradeDto object to store the grades
        StudentGradeDto studentGradeDto = new StudentGradeDto(user.getLastName() + " " + user.getName());
        studentGradeDto.setFinalGrade(0f); // TODO: Fix this calculation
        studentGradeDto.setPracticeGrades(new LinkedHashMap<>());

        // Iterate through the practices and add the grades to the StudentGradeDto
        // object
        for (Practice practice : practices) {
            if (Boolean.TRUE.equals(practice.getGradeable())) { // Check if the practice is gradeable
                // Check if the user has a simulation for this practice
                for (Simulation simulation : userSimulations) {
                    if (simulation.getPractice().equals(practice)) {
                        Float grade = simulation.getGrade();
                        if (grade != null) {
                            studentGradeDto.addPracticeGrade(practice.getName(), grade);
                            studentGradeDto.setFinalGrade(0f);
                        } else {
                            studentGradeDto.addPracticeGrade(practice.getName(), null);
                        }
                    }
                }
            }
        }

        // Ensure that all practices are included in the StudentGradeDto object
        for (Practice practice : practices) {
            if (Boolean.TRUE.equals(practice.getGradeable())
                    && !studentGradeDto.getPracticeGrades().containsKey(practice.getName())) {
                studentGradeDto.addPracticeGrade(practice.getName(), null);
            }
        }

        // Return the StudentGradeDto object
        return studentGradeDto;
    }

    public void updateClassGradePercentages(PracticesPercentagesDto practicesPercentagesDto) {
        for (PracticePercentageDto practicePercentage : practicesPercentagesDto.getPracticesPercentages()) {
            log.info("Updating grade percentage for practice ID: {}", practicePercentage.getPracticeId());
            Practice practice = practiceRepository.findById(practicePercentage.getPracticeId())
                    .orElseThrow(() -> new EntityNotFoundException("Practice not found"));
            // Verifies if the percentages add up to 100
            if (practicePercentage.getPercentage() < 0 || practicePercentage.getPercentage() > 100) {
                throw new RuntimeException("Invalid percentage for practice ID: " + practicePercentage.getPracticeId());
            }
            Float totalPercentage = 0f;
            for (PracticePercentageDto pp : practicesPercentagesDto.getPracticesPercentages()) {
                if (pp.getPracticeId() != practicePercentage.getPracticeId()) {
                    totalPercentage += pp.getPercentage();
                }
            }
            if (totalPercentage + practicePercentage.getPercentage() > 100) {
                throw new RuntimeException("Total percentage exceeds 100 for practice ID: " + practicePercentage.getPracticeId());
            }
            practice.setGradePercentage(practicePercentage.getPercentage());
            practiceRepository.save(practice);
        }
    }
}
