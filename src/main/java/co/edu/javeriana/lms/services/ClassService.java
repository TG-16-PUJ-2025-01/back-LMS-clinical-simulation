package co.edu.javeriana.lms.services;

import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import co.edu.javeriana.lms.dtos.ClassModelDTO;
import co.edu.javeriana.lms.models.ClassModel;
import co.edu.javeriana.lms.repositories.ClassModelRepository;
import co.edu.javeriana.lms.repositories.CourseRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClassService {
    
    @Autowired
    private ClassModelRepository classRepository;


    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<ClassModel> findAll(Integer page, Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return classRepository.findAll(pageable);
    }

    private String definePeriod(Date beginningDate) {

        if (beginningDate == null) {
            throw new IllegalArgumentException("Beginning date cannot be null");
        }
        
        log.info("REVISAR BIEN UNICORNIO"+ beginningDate.toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginningDate);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = calendar.get(Calendar.YEAR);

        if (month >= 1 && month <5) { // January - February
            return year + "-10";
        } else if (month >= 5 && month <9) { // May - June
            return year + "-20";
        } else if (month >= 9 && month <=12) { // September - October
            return year + "-30";
        }

        throw new IllegalArgumentException("Date does not match any academic period");
    }

    public ClassModel findById(Long id) {

        return classRepository.findById(id).get();
    }  
   
        
    public ClassModel save(ClassModelDTO entity) {
        ClassModel classModel = new ClassModel(entity.getName(), entity.getBeginningDate(), userRepository.findById(entity.getProfessorId()).get(), courseRepository.findById(entity.getCourseId()).get(), entity.getIdJaveriana());
        classRepository.save(classModel);
        return classModel;
    }

    public void deleteById(Long id) {
        
        classRepository.deleteById(id);
    }

    public ClassModel update(ClassModelDTO classModel) {
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

        return currentClassModel;
    }

}
