package co.edu.javeriana.lms.grades.services;

import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.grades.dtos.RubricTemplateDTO;
import co.edu.javeriana.lms.grades.models.Criteria;
import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import co.edu.javeriana.lms.grades.repositories.RubricTemplateRepository;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RubricTemplateService {

    @Autowired
    private RubricTemplateRepository rubricTemplateRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private RubricRepository rubricRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<RubricTemplate> findAll(String filter, Integer page, Integer size, String sort, Boolean asc, Boolean mine, Boolean archived, String userEmail) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        User creator = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        //FALTA SI ES ADMIN ACA SE ENVIA TODO

        //SI SOLO QUIERE VER SUS ARCHIVADOS
        if(mine && archived)
            return rubricTemplateRepository.findArchivedMineByTitleOrCreationDateContaining(filter, archived, creator.getId(), pageable);
        //SI QUIERE VER TODOS LOS SUYOS
        else if (mine && !archived)
            return rubricTemplateRepository.findMineByTitleOrCreationDateContaining(filter, creator.getId(), pageable);
        //SI QUIERE VER LOS DE OTROS Menos los archivados
        else
            return rubricTemplateRepository.findNotMineByTitleOrCreationDateContaining(filter, creator.getId(), pageable);

    }

    
    public RubricTemplate findById(Long id) {

        return rubricTemplateRepository.findById(id).get();
    }

    public RubricTemplate archiveById(Long id) {
        RubricTemplate rubricTemplate=rubricTemplateRepository.findById(id).get();;
        rubricTemplate.setArchived(true);
        return rubricTemplateRepository.save(rubricTemplate);
    }

    public RubricTemplate save(RubricTemplateDTO rubricTemplate, String userEmail) {

        RubricTemplate rubricTemplateModel = new RubricTemplate();
        rubricTemplateModel.setTitle(rubricTemplate.getTitle());

        //SE LE ANADEN LOS IDS A LOS CRITERIOS IMPORTANTE
        rubricTemplateModel.setCriteria(addCriteriaUUID(rubricTemplate.getCriteria()));
        
        if(rubricTemplate.getCourses()!=null)
            rubricTemplateModel.setCourses(courseRepository.findAllById(rubricTemplate.getCourses()));
        
        rubricTemplateModel.setCreationDate(rubricTemplate.getCreationDate());
        
        if(rubricTemplate.getPracticeId()!=null)
            rubricTemplateModel.setPractice(practiceRepository.findById(rubricTemplate.getPracticeId()).get());
        
        rubricTemplateModel.setArchived(rubricTemplate.getArchived());

        //asignar el creador por el principal
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        rubricTemplateModel.setCreator(creator);
        
        return rubricTemplateRepository.save(rubricTemplateModel);
    }
             
    private List<Criteria> addCriteriaUUID(List<Criteria> criteria) {
        for (Criteria criteria2 : criteria) {
            criteria2.setId(java.util.UUID.randomUUID());
        }
        return criteria;
    }
           
    public void deleteById(Long id) {
        // Buscar la plantilla por ID de manera segura
        RubricTemplate rubricTemplate = rubricTemplateRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rubric Template not found with ID: " + id));
    
        // Validar si ya tiene rubrias calificadas
        //si el año de creacion es tres años mayor al actual, se puede eliminar
        if (rubricTemplate.getRubrics().size() > 0 && rubricTemplate.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() - 2 > 0) {
            
            throw new IllegalStateException("Cannot delete rubric template because there are Already rubrics created with this template");
        }
    
        // Si cumple las condiciones, se elimina
        rubricTemplateRepository.deleteById(id);
    }

    //REVISAR ADD TO PRACTICE Y ADD COURSES
    public RubricTemplate update(RubricTemplateDTO rubricTemplate, Long id, Long practiceId) {

        RubricTemplate rubricTemplateModel = rubricTemplateRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rubric Template not found with ID: " + id));

        /*If rubric template is only assigned to a practice is from a professor*/
        rubricTemplateModel.setTitle(rubricTemplate.getTitle());
        List<Criteria> previousCriteria = rubricTemplate.getCriteria();
        rubricTemplateModel.setCriteria(rubricTemplate.getCriteria());
        rubricTemplateModel.setCourses(courseRepository.findAllById(rubricTemplate.getCourses()));
        rubricTemplateModel.setCreationDate(rubricTemplate.getCreationDate());
        rubricTemplateModel.setArchived(rubricTemplate.getArchived());
        
        //update the rubric based on the rubric template edition
        //recorrer el nuevo criteria y compara con el criteria de la rubrica
        for (Rubric rubric : rubricTemplateModel.getRubrics()) {
            //recorrer el criteria de la rubrica
            rubric.changeEvaluatedCriteria(previousCriteria);

            //actualizar la rubrica
            rubricRepository.save(rubric);
        }

        return rubricTemplateRepository.save(rubricTemplateModel);
    }            
    
    //all courses are added
    public RubricTemplate updaterubricTemplateCourses(List<Course> coursesToAdd, Long id) {
        RubricTemplate rubricTemplate = rubricTemplateRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Rubric Template not found with ID: " + id));
        
        rubricTemplate.getCourses().clear();
        rubricTemplate.getCourses().addAll(coursesToAdd);
        return rubricTemplateRepository.save(rubricTemplate);
    }
    
}
