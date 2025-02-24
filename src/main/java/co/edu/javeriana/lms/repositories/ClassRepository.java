package co.edu.javeriana.lms.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import co.edu.javeriana.lms.models.ClassModel;

@Repository
public interface ClassRepository extends JpaRepository <ClassModel, Long> {
    @Query("""
        SELECT c FROM ClassModel c 
        WHERE CAST(c.javerianaId AS string) LIKE %:filter% 
        OR LOWER(TRANSLATE(c.name, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
    """)
    Page<ClassModel> findByNameOrJaverianaIdContaining(@Param("filter") String filter, Pageable pageable);
}
