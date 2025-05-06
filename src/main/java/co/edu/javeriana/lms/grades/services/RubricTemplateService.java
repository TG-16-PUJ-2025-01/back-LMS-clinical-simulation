package co.edu.javeriana.lms.grades.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.grades.dtos.RubricTemplateDTO;
import co.edu.javeriana.lms.grades.models.Criteria;
import co.edu.javeriana.lms.grades.models.EvaluatedCriteria;
import co.edu.javeriana.lms.grades.models.Rubric;
import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.grades.repositories.RubricRepository;
import co.edu.javeriana.lms.grades.repositories.RubricTemplateRepository;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
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

    public Page<RubricTemplate> findAll(String filter, Integer page, Integer size, String sort, Boolean asc,
            Boolean archived, String userEmail) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        // SI ES ADMIN ACA SE ENVIA TODO

        if (archived) {

            if (creator.getRoles().contains(Role.ADMIN)) {
                return rubricTemplateRepository.findArchivedByTitleOrCreationDateContaining(filter,
                        pageable);
            } else {
                return rubricTemplateRepository.findArchivedMineByTitleOrCreationDateContaining(filter,
                        creator.getId(), pageable);
            }
        } else {
            return rubricTemplateRepository.findNotArchivedByTitleOrCreationDateContaining(filter,
                    pageable);
        }
    }

    public RubricTemplate findById(Long id) {

        return rubricTemplateRepository.findById(id).get();
    }

    public RubricTemplate archiveById(Long id) {
        RubricTemplate rubricTemplate = rubricTemplateRepository.findById(id).get();
        ;
        rubricTemplate.setArchived(true);
        return rubricTemplateRepository.save(rubricTemplate);
    }

    public RubricTemplate unarchiveById(Long id) {
        RubricTemplate rubricTemplate = rubricTemplateRepository.findById(id).get();
        ;
        rubricTemplate.setArchived(false);
        return rubricTemplateRepository.save(rubricTemplate);
    }

    public RubricTemplate save(RubricTemplateDTO rubricTemplate, String userEmail) {

        RubricTemplate rubricTemplateModel = new RubricTemplate();
        rubricTemplateModel.setTitle(rubricTemplate.getTitle());

        // SE LE ANADEN LOS IDS A LOS CRITERIOS IMPORTANTE
        rubricTemplateModel.setCriteria(addCriteriaUUID(rubricTemplate.getCriteria()));
        rubricTemplateModel.setColumns(rubricTemplate.getColumns());

        if (rubricTemplate.getCourses() != null || rubricTemplate.getCourses().size() > 0)
            rubricTemplateModel.setCourses(courseRepository.findAllById(rubricTemplate.getCourses()));

        rubricTemplateModel.setCreationDate(new Date());

        Boolean isForPractice = false;
        Practice practice = new Practice();
        if (rubricTemplate.getPracticeId() != null) {
            practice = practiceRepository.findById(rubricTemplate.getPracticeId()).get();
            rubricTemplateModel.setPractice(practice);
            isForPractice = true;
        }
        rubricTemplateModel.setArchived(rubricTemplate.getArchived());

        // asignar el creador por el principal
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        rubricTemplateModel.setCreator(creator);

        RubricTemplate savedRubricTemplate = rubricTemplateRepository.save(rubricTemplateModel);

        if (isForPractice) {
            createRubricsFromTemplate(rubricTemplateModel, practice);
        }

        return savedRubricTemplate;
    }

    private void createRubricsFromTemplate(RubricTemplate rubricTemplateModel, Practice practice) {
        // create the rubric based on the rubric template
        Rubric newRubric = new Rubric();
        newRubric.setRubricTemplate(rubricTemplateModel);
        newRubric.setEvaluatedCriterias(new ArrayList<EvaluatedCriteria>());
        rubricTemplateModel.getCriteria().forEach(criteria -> {
            newRubric.addEvaluatedCriteria(new EvaluatedCriteria(criteria.getId(), "", 0F));
        });

        // get the simulations from practice
        for (Simulation simulation : practice.getSimulations()) {
            newRubric.setSimulation(simulation);
            rubricRepository.save(newRubric);
        }
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

        Calendar cal = Calendar.getInstance();
        cal.setTime(rubricTemplate.getCreationDate());
        Integer yearsDifference = LocalDate.now().getYear() - cal.get(Calendar.YEAR);

        // Validar si ya tiene rúbricas calificadas
        if (rubricTemplate.getRubrics().size() > 0 && yearsDifference < 3) {
            throw new IllegalStateException(
                    "Cannot delete rubric template because there are already rubrics created with this template");
        }

        rubricTemplate.getRubrics().forEach(rubric -> rubric.setRubricTemplate(null));

        rubricTemplateRepository.save(rubricTemplate); // Guardar los cambios en las rúbricas

        // Si cumple las condiciones, se elimina
        rubricTemplateRepository.deleteById(id);
    }

    // REVISAR ADD TO PRACTICE Y ADD COURSES
    public RubricTemplate update(RubricTemplateDTO rubricTemplate, Long id) {

        RubricTemplate rubricTemplateModel = rubricTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rubric Template not found with ID: " + id));

        /* If rubric template is only assigned to a practice is from a professor */
        rubricTemplateModel.setTitle(rubricTemplate.getTitle());
        List<Criteria> previousCriteria = rubricTemplate.getCriteria();
        rubricTemplateModel.setCriteria(rubricTemplate.getCriteria());
        rubricTemplateModel.setCourses(courseRepository.findAllById(rubricTemplate.getCourses()));
        rubricTemplateModel.setArchived(rubricTemplate.getArchived());

        // update the rubric based on the rubric template edition
        // recorrer el nuevo criteria y compara con el criteria de la rubrica
        for (Rubric rubric : rubricTemplateModel.getRubrics()) {
            // recorrer el criteria de la rubrica
            rubric.changeEvaluatedCriteria(previousCriteria);

            // actualizar la rubrica
            rubricRepository.save(rubric);
        }

        return rubricTemplateRepository.save(rubricTemplateModel);
    }

    // all courses are added
    public RubricTemplate updateRubricTemplateCourses(List<Course> coursesToAdd, Long id) {
        RubricTemplate rubricTemplate = rubricTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rubric Template not found with ID: " + id));

        rubricTemplate.getCourses().clear();
        rubricTemplate.getCourses().addAll(coursesToAdd);
        return rubricTemplateRepository.save(rubricTemplate);
    }

    public List<RubricTemplate> findRecommendedRubricTemplatesByCoursesById(Long id) {
        return rubricTemplateRepository.findRecommendedRubricTemplatesByCoursesById(id);
    }
}
