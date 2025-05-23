package co.edu.javeriana.lms.practices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.subjects.models.ClassModel;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {

    Page<Practice> findByNameContaining(String name, Pageable pageable);
    
    List<Practice> findByClassModel_ClassId(Long classId, Sort sort);

    List<Practice> findByClassModel_ClassId(Long classId);

    List<Practice> findByClassModelIn(List<ClassModel> classModels);
}