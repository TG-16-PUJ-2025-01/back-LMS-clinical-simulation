package co.edu.javeriana.lms.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import co.edu.javeriana.lms.dtos.ClassModelDTO;
import co.edu.javeriana.lms.models.ClassModel;
import co.edu.javeriana.lms.repositories.ClassModelRepository;
import co.edu.javeriana.lms.repositories.CourseRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClassService implements CrudService<ClassModelDTO, Long>{
    
    @Autowired
    private ClassModelRepository classRepository;


    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ClassModelDTO> findAll(Integer page, Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return createDTOsList(classRepository.findAll(pageable).getContent());
    }

    private List<ClassModelDTO> createDTOsList(List<ClassModel> classes) {
        return classes.stream()
        .map(classModel -> new ClassModelDTO(
            classModel.getId(),  // Pass id here
            classModel.getIdJaveriana(),
            classModel.getName(),
            classModel.getProfessor().getName(),
            classModel.getProfessor().getId(),
            classModel.getCourse().getName(),
            classModel.getCourse().getId(),
            definePeriod(classModel.getBeginningDate()), 
            classModel.getBeginningDate()
        ))
        .toList();    
    }
        
    private String definePeriod(Date beginningDate) {
        if (beginningDate == null) {
            throw new IllegalArgumentException("Beginning date cannot be null");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginningDate);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = calendar.get(Calendar.YEAR);

        if (month >= 1 && month <= 2) { // January - February
            return year + "-10";
        } else if (month >= 5 && month <= 6) { // May - June
            return year + "-20";
        } else if (month >= 9 && month <= 10) { // September - October
            return year + "-30";
        }

        throw new IllegalArgumentException("Date does not match any academic period");
    }

        
    public Long countClasses() {
        return classRepository.count();
    }

    public ClassModelDTO findById(Long id) {

        return createDTO(classRepository.findById(id).get());
    }
        
    private ClassModelDTO createDTO(ClassModel classModel) {
        return new ClassModelDTO(
            classModel.getId(),  // Pass id here
            classModel.getIdJaveriana(),
            classModel.getName(),
            classModel.getProfessor().getName(),
            classModel.getProfessor().getId(),
            classModel.getCourse().getName(),
            classModel.getCourse().getId(),
            definePeriod(classModel.getBeginningDate()), 
            classModel.getBeginningDate()
        );
    }
        
    public ClassModelDTO save(ClassModelDTO entity) {
        ClassModel classModel = new ClassModel(entity.getName(), entity.getBeginningDate(), userRepository.findById(entity.getProfessorId()).get(), courseRepository.findById(entity.getCourseId()).get(), entity.getIdJaveriana());
        classRepository.save(classModel);
        return entity;
    }

    public void deleteById(Long id) {
        classRepository.deleteById(id);
    }

    public void update(ClassModelDTO classModel) {
        ClassModel currentClassModel = classRepository.findById(classModel.getId())
                .orElseThrow(() -> new EntityNotFoundException("Class with ID " + classModel.getId() + " not found"));

        // Check for null values before updating
        if (classModel.getBeginningDate() == null || 
            classModel.getName() == null || 
            classModel.getProfessorName() == null ||
            classModel.getCourseName() == null ||
            classModel.getPeriod() == null ||
            classModel.getIdJaveriana() == null ||
            classModel.getProfessorId() == null ||
            classModel.getCourseId() == null
            ) {
            
            throw new IllegalArgumentException("Error: All fields must have values. Null values are not allowed.");
        }

        // Update fields
        currentClassModel.setBeginningDate(classModel.getBeginningDate());
        currentClassModel.setName(classModel.getName());
        currentClassModel.setProfessor(userRepository.findById(classModel.getProfessorId()).get());
        currentClassModel.setCourse(courseRepository.findById(classModel.getCourseId()).get());

        classRepository.save(currentClassModel);
    }

}
