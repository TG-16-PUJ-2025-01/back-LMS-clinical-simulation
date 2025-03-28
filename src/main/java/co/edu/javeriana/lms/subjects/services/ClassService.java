package co.edu.javeriana.lms.subjects.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.subjects.dtos.ClassDto;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
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
        return classRepository.searchClasses(filter, pageable);
    }

    public Page<User>findAllMembers(String filter, Integer page, Integer size, String sort, Boolean asc, Long id, String role)
    {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        if(role.equals(Role.PROFESOR.name()))
        {
            return classRepository.findProfessorsMembers(id, filter, pageable).map(user -> {
                user.getRoles().clear();
                ArrayList<Role> roles = new ArrayList<>();
                roles.add(Role.PROFESOR);
                user.setRoles(new HashSet<>(roles));
                return user;
            });
        }
        else if(role.equals(Role.ESTUDIANTE.name()))
        {
            return classRepository.findStudentsMembers(id, filter, pageable).map(user -> {
                user.getRoles().clear();
                ArrayList<Role> roles = new ArrayList<>();
                roles.add(Role.ESTUDIANTE);
                user.setRoles(new HashSet<>(roles));
                return user;
            });
        }
        else
        {
            ClassModel classModel = classRepository.findById(id).get();
            Page<User> members = classRepository.findMembers(id, filter, pageable);

            //iterar sobre los miembros de la clase y manipular el rol
            //si pertenece a profesor, se le asigna el rol de profesor
            //si pertenece a estudiante, se le asigna el rol de estudiante

            members.forEach(user -> {
                if(classModel.getProfessors().contains(user))
                {
                    user.getRoles().clear();
                    ArrayList<Role> roles = new ArrayList<>();
                    roles.add(Role.PROFESOR);
                    user.setRoles(new HashSet<>(roles));
                }
                else if(classModel.getStudents().contains(user))
                {
                    user.getRoles().clear();
                    ArrayList<Role> roles = new ArrayList<>();
                    roles.add(Role.ESTUDIANTE);
                    user.setRoles(new HashSet<>(roles));
                }
            });

            return members;
        }

    }

    public Page<User>findAllNonMembers(String filter, Integer page, Integer size, String sort, Boolean asc, Long id,String role)
    {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        
        if(role.equals(Role.PROFESOR.name()))
        {
            return classRepository.findProfessorsNotInClass(id, filter, pageable).map(user -> {
                user.getRoles().clear();
                ArrayList<Role> roles = new ArrayList<>();
                roles.add(Role.PROFESOR);
                user.setRoles(new HashSet<>(roles));
                return user;
            });
        }
        else if(role.equals(Role.ESTUDIANTE.name()))
        {
            return classRepository.findStudentsNotInClass(id, filter, pageable).map(user -> {
                user.getRoles().clear();
                ArrayList<Role> roles = new ArrayList<>();
                roles.add(Role.ESTUDIANTE);
                user.setRoles(new HashSet<>(roles));
                return user;
            });
        }
        else
            return classRepository.findUsersNotInClass(id, filter, pageable).map(user -> {
                user.getRoles().remove(Role.COORDINADOR);
                user.setRoles(user.getRoles());
                return user;
            });
    }

    public ClassModel findById(Long id) {

        return classRepository.findById(id).get();
    }

    public ClassModel save(ClassDto entity) {

        List <User> professors = new ArrayList<>();
        //mappeo de profesores por stream
        entity.getProfessorsIds().stream().forEach(professorId -> {
            professors.add(userRepository.findById(professorId).get());
        });
        
        ClassModel classModel = new ClassModel(entity.getPeriod(),
                professors,
                courseRepository.findById(entity.getCourseId()).get(), entity.getJaverianaId(), entity.getNumberOfParticipants());
       
       // log.info("unicornio aa2 "+ classModel.getJaverianaId(), classModel.getName(), classModel.getBeginningDate(), classModel.getProfessor().getName(), classModel.getCourse().getCourseId());

        classRepository.save(classModel);

        return classModel;
    }

    public void deleteById(Long id) {

        classRepository.deleteById(id);
    }

    public ClassModel update(ClassModel classModel) { 
        classRepository.save(classModel);

        return classModel;
    }

    public ClassModel fromDtoToClass(ClassDto classModeldto, Long id)
    {
        ClassModel currentClassModel = classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class with ID " + id + " not found"));
       

        currentClassModel.setJaverianaId(classModeldto.getJaverianaId());
        // Update fields
        currentClassModel.setPeriod(classModeldto.getPeriod());
        currentClassModel.setCourse(courseRepository.findById(classModeldto.getCourseId()).get());
        currentClassModel.setNumberOfParticipants(classModeldto.getNumberOfParticipants());
        return currentClassModel;
    }

    public ClassModel updateMembers(List<User> members, Long id) {

        ClassModel classModel = classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class with ID " + id + " not found"));

        //anadir los profesores a la clase en members buscando precisamente los profesores con role
        members.stream().forEach(member -> {
            if(member.getRoles().contains(Role.PROFESOR))
            {
                classModel.getProfessors().add(userRepository.findById(member.getId()).get());
            }
            else if(member.getRoles().contains(Role.ESTUDIANTE))
            {
                classModel.getStudents().add(userRepository.findById(member.getId()).get());
            }
        });

        classRepository.save(classModel);
        
        return classModel;
    }

    public List<ClassModel> findByProfessorId(Long userId) {
        return classRepository.findByProfessors_Id(userId);
    }

    public List<ClassModel> findByProfessorIdAndFilters(Long userId, Integer year, Integer period) {
        List<ClassModel> classes = classRepository.findByProfessors_Id(userId);

        if (year != null) {
            classes = classes.stream()
                    .filter(c -> c.getPeriod().startsWith(year.toString()))
                    .toList();
        }

        if (period != null) {
            classes = classes.stream()
                    .filter(c -> c.getPeriod().endsWith(period.toString()))
                    .toList();
        }

        List<ClassModel> mutableClasses = new ArrayList<>(classes);
        mutableClasses.sort((c1, c2) -> c2.getPeriod().compareTo(c1.getPeriod()));

        return mutableClasses;
    }

}
