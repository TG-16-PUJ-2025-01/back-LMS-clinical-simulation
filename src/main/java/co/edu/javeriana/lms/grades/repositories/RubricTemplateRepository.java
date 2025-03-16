package co.edu.javeriana.lms.grades.repositories;

import co.edu.javeriana.lms.grades.models.RubricTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RubricTemplateRepository extends JpaRepository<RubricTemplate, Long> {
    
    @Query("""
        SELECT c FROM RubricTemplate c 
        WHERE 
            (LOWER(TRANSLATE(c.title, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
            OR CAST(c.creationDate AS string) LIKE CONCAT('%', :filter, '%'))
            AND c.archived = :archived
            AND c.creator.id = :creatorId
    """)
    Page<RubricTemplate> findArchivedMineByTitleOrCreationDateContaining(
        @Param("filter") String filter, 
        @Param("archived") Boolean archived, 
        @Param("creatorId") Long creatorId, 
        Pageable pageable
    );

    
    @Query("""
        SELECT c FROM RubricTemplate c 
        WHERE 
            (LOWER(TRANSLATE(c.title, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
            OR CAST(c.creationDate AS string) LIKE CONCAT('%', :filter, '%'))
            AND c.creator.id = :creatorId
    """)
    Page<RubricTemplate> findMineByTitleOrCreationDateContaining(@Param("filter") String filter,  @Param("creatorId") Long creatorId, Pageable pageable);
    
    
    @Query("""
        SELECT c FROM RubricTemplate c 
        WHERE 
            (LOWER(TRANSLATE(c.title, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
            OR CAST(c.creationDate AS string) LIKE CONCAT('%', :filter, '%'))
            AND c.creator.id != :creatorId
    """)
    Page<RubricTemplate> findNotMineByTitleOrCreationDateContaining(@Param("filter") String filter, @Param("creatorId") Long creatorId, Pageable pageable);


    @Query("""
        SELECT rt 
        FROM Practice p 
        JOIN p.classModel cm 
        JOIN cm.course c 
        JOIN c.rubricTemplates rt
        WHERE p.id = :id
    """)
    List<RubricTemplate> findRecommendedRubricTemplatesByCoursesById(@Param("id") Long id);
    
    @Query("""
        SELECT c FROM RubricTemplate c 
        WHERE 
            (LOWER(TRANSLATE(c.title, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
            OR CAST(c.creationDate AS string) LIKE CONCAT('%', :filter, '%'))
    """)
    Page<RubricTemplate> findAllByTitleOrCreationDateContaining(@Param("filter") String filter, Pageable pageable);

}
