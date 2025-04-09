package co.edu.javeriana.lms.grades.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.grades.dtos.StudentGradeDto;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GradeService {
    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private ClassRepository classRepository;


    public List<StudentGradeDto> getFinalGradesByClass(Long classModelId) {
        log.info("Fetching final grades for class with ID: {}", classModelId);
    
        ClassModel classModel = classRepository.findById(classModelId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
    
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
    
        // Paso 3: Procesar simulaciones con nota
        for (Simulation simulation : simulations) {
            Practice practice = simulation.getPractice();
            if (!Boolean.TRUE.equals(practice.getGradeable())) continue;
    
            Float grade = simulation.getGrade();
            if (grade == null) continue;
    
            Float gradePercentage = practice.getGradePercentage();
            if (gradePercentage == null) gradePercentage = 0f;
    
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
    
        // Paso 4: Asegurar que cada estudiante tenga una entrada para todas las prácticas
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
}
