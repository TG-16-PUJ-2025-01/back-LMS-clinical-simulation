package co.edu.javeriana.lms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.repositories.ClassRepository;
import co.edu.javeriana.lms.repositories.CourseRepository;
import co.edu.javeriana.lms.repositories.UserRepository;
import co.edu.javeriana.lms.subjects.dtos.ClassDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<ClassModel> findAll(String filter, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return classRepository.findAll(pageable);
    }

    public ClassModel findById(Long id) {

        return classRepository.findById(id).get();
    }

    public ClassModel save(ClassDto entity) {

        log.info("unicornio aa "+ entity);
        
        ClassModel classModel = new ClassModel(entity.getName(), entity.getBeginningDate(),
                userRepository.findById(entity.getProfessorId()).get(),
                courseRepository.findById(entity.getCourseId()).get(), entity.getJaverianaId());
       
       // log.info("unicornio aa2 "+ classModel.getJaverianaId(), classModel.getName(), classModel.getBeginningDate(), classModel.getProfessor().getName(), classModel.getCourse().getCourseId());

        classRepository.save(classModel);


        log.info("unicornio aa3 "+ entity);

        return classModel;
    }

    public void deleteById(Long id) {

        classRepository.deleteById(id);
    }

    public ClassModel update(ClassDto classModel, Long id) {

        ClassModel currentClassModel = classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class with ID " + id + " not found"));

        // Update fields
        currentClassModel.setBeginningDate(classModel.getBeginningDate());
        currentClassModel.setName(classModel.getName());
        currentClassModel.setProfessor(userRepository.findById(classModel.getProfessorId()).get());
        currentClassModel.setCourse(courseRepository.findById(classModel.getCourseId()).get());

        classRepository.save(currentClassModel);

        return currentClassModel;
    }

}
