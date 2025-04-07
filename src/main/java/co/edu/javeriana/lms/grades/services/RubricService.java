package co.edu.javeriana.lms.grades.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.grades.dtos.RubricDto;
import co.edu.javeriana.lms.grades.models.Rubric;
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

    public Rubric create(RubricDto rubricDto) {
        return rubricRepository.save(rubricDto.toRubric());
    }

    public Rubric update(RubricDto rubricDto, Long id) {
        rubricRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rubric not found"));

        Rubric rubric = rubricDto.toRubric();
        rubric.setRubricId(id);

        return rubricRepository.save(rubric);
    }

}
