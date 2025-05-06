package co.edu.javeriana.lms.practices.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.grades.repositories.RubricTemplateRepository;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.practices.models.Simulation;

@Slf4j
@Service
public class PracticeService {

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private RubricTemplateRepository rubricTemplateRepository;

    public Page<Practice> findAll(String keyword, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOder);
        return practiceRepository.findByNameContaining(keyword, pageable);
    }

    public Practice findById(Long id) {
        return practiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + id));
    }

    public Practice save(Long id, Practice practice) {

        classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + id));

        ClassModel classModel = classRepository.findById(id).get();

        practice.setClassModel(classModel);

        return practiceRepository.save(practice);
    }

    public void deleteById(Long id) {
        if(!practiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Practice not found with id: " + id);
        }
        practiceRepository.deleteById(id);
    }

    public Practice update(Long id, Practice practice) {
        Practice existingPractice = practiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + id));

        ClassModel classModel = existingPractice.getClassModel();
        practice.setClassModel(classModel);
        practice.setId(id);

        return practiceRepository.save(practice);
    }

    public List<Practice> findByClassId(Long classId) {
        classRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + classId));

        List<Practice> practices = practiceRepository.findByClassModel_ClassId(classId, Sort.by(Sort.Direction.ASC, "id"));

        return practices;
    }

    public Practice updateRubric(Long id, Long rubricId) {
        Practice existingPractice = practiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + id));
        
        RubricTemplate rubric = rubricTemplateRepository.findById(rubricId)
                .orElseThrow(() -> new EntityNotFoundException("Rubric not found with id: " + rubricId));

        existingPractice.setRubricTemplate(rubric);
        practiceRepository.save(existingPractice);

        return existingPractice;
    }

    public Long getEnroledSimulation(Long practiceId, Long userId) {
        Practice practice = practiceRepository.findById(practiceId)
                .orElseThrow(() -> new EntityNotFoundException("Practice not found with id: " + practiceId));

        // Iterate through simulations to find the one where the user is enrolled
        for (Simulation simulation : practice.getSimulations()) {
            for (User user : simulation.getUsers()) {
                if (user.getId().equals(userId)) {
                    return simulation.getSimulationId();
                }
            }
        }

        // Return null if no simulation is found
        return null;
    }
}
