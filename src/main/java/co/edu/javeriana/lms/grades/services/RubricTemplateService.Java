package co.edu.javeriana.lms.grades.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.javeriana.lms.grades.dtos.RubricTemplateDTO;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.grades.repositories.RubricTemplateRepository;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RubricTemplateService {

    @Autowired
    private RubricTemplateRepository rubricTemplateRepository;

    @Autowired
    private CourseRepository courseRepository;


    public Page<RubricTemplate> findAll(String filter, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return rubricTemplateRepository.findByTitleOrCreationDateContaining(filter, pageable);
    }

    
    public RubricTemplate findById(Long id) {

        return rubricTemplateRepository.findById(id).get();
    }

    public RubricTemplate archiveById(Long id) {

        //evaluar si 
        RubricTemplate rubricTemplate=rubricTemplateRepository.findById(id).get();;
        rubricTemplate.setArchived(true);
        return rubricTemplateRepository.save(rubricTemplate);
    }


    public RubricTemplate save(RubricTemplateDTO rubricTemplate) {
        RubricTemplate rubricTemplateModel = new RubricTemplate();
        rubricTemplateModel.setTitle(rubricTemplate.getTitle());
        rubricTemplateModel.setCriteria(rubricTemplate.getCriteria());
        //mapeo de id de cursos para crear una lista de cursos que se obtiene por find by id
        //se usa courseRepository para obtener los cursos
        rubricTemplateModel.setCourses(courseRepository.findAllById(rubricTemplate.getCourses()));
        rubricTemplateModel.setCreationDate(rubricTemplate.getCreationDate());
       // rubricTemplateModel.addToPractices(rubricTemplate.getPracticeId());
        rubricTemplateModel.setArchived(rubricTemplate.getArchived());
        return rubricTemplateRepository.save(rubricTemplateModel);
    }
}
