package co.edu.javeriana.lms.grades.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.grades.models.EvaluatedCriteria;
import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RubricService {
    
@Autowired
private RubricRepository rubricRepository;

public Rubric findById(Long id) {

    return rubricRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Rubric not found"));
}

public Rubric update(List<EvaluatedCriteria> evaluatedCriterias, Long id) {
    Rubric foundRubric= rubricRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rubric not found"));

    foundRubric.setEvaluatedCriterias(evaluatedCriterias);

    return rubricRepository.save(foundRubric);
}
    
}
