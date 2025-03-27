package co.edu.javeriana.lms.subjects.repositories;

import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

@Repository
public interface ClassRepository extends JpaRepository<ClassModel, Long> {
    @Query("""
                SELECT DISTINCT c FROM ClassModel c
                JOIN c.course course
                LEFT JOIN c.professors professor
                WHERE (:filter IS NULL OR CAST(c.javerianaId AS string) LIKE %:filter%
                    OR LOWER(c.period) LIKE LOWER(CONCAT('%', :filter, '%'))
                    OR LOWER(course.name) LIKE LOWER(CONCAT('%', :filter, '%'))
                    OR LOWER(professor.name) LIKE LOWER(CONCAT('%', :filter, '%'))
                    OR LOWER(professor.lastName) LIKE LOWER(CONCAT('%', :filter, '%')))
            """)
    Page<ClassModel> searchClasses(@Param("filter") String filter, Pageable pageable);

    // obtener todos los miembros de una clase
    @Query("""
                SELECT u FROM User u
                WHERE (
                    u.id IN (
                        SELECT s.id FROM ClassModel c JOIN c.students s WHERE c.classId = :classId
                    )
                    OR u.id IN (
                        SELECT p.id FROM ClassModel c JOIN c.professors p WHERE c.classId = :classId
                    )
                )
                AND (
                    :filter IS NULL OR :filter = '' OR
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    CAST(u.institutionalId AS string) LIKE %:filter%
                )
            """)
    Page<User> findMembers(@Param("classId") Long classId, @Param("filter") String filter, Pageable pageable);

    @Query("""
                SELECT u FROM User u
                WHERE (
                    u.id IN (
                        SELECT s.id FROM ClassModel c JOIN c.students s WHERE c.classId = :classId
                    )

                )
                AND (
                    :filter IS NULL OR :filter = '' OR
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    CAST(u.institutionalId AS string) LIKE %:filter%
                )
            """)
    Page<User> findStudentsMembers(@Param("classId") Long classId, @Param("filter") String filter, Pageable pageable);

    @Query("""
                SELECT u FROM User u
                WHERE (
                    u.id IN (
                        SELECT p.id FROM ClassModel c JOIN c.professors p WHERE c.classId = :classId
                    )
                )
                AND (
                    :filter IS NULL OR :filter = '' OR
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    CAST(u.institutionalId AS string) LIKE %:filter%
                )
            """)
    Page<User> findProfessorsMembers(@Param("classId") Long classId, @Param("filter") String filter, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE u.id NOT IN (
                SELECT s.id FROM ClassModel c JOIN c.students s WHERE c.classId = :classId
            )
            AND u.id NOT IN (
                SELECT p.id FROM ClassModel c JOIN c.professors p WHERE c.classId = :classId
            )
            AND NOT (
                'COORDINADOR' MEMBER OF u.roles AND SIZE(u.roles) = 1
            )
            AND (
                :filter IS NULL OR :filter = '' OR
                LOWER(u.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                CAST(u.institutionalId AS string) LIKE %:filter%
            )
            """)
    Page<User> findUsersNotInClass(@Param("classId") Long classId, @Param("filter") String filter, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE u.id NOT IN (
                SELECT s.id FROM ClassModel c JOIN c.students s WHERE c.classId = :classId
            )
            AND u.id NOT IN (
                SELECT p.id FROM ClassModel c JOIN c.professors p WHERE c.classId = :classId
            )
            AND NOT (
                'COORDINADOR' MEMBER OF u.roles AND SIZE(u.roles) = 1
            )
            AND (
                'PROFESOR' MEMBER OF u.roles
            )
            AND (
                :filter IS NULL OR :filter = '' OR
                LOWER(u.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                CAST(u.institutionalId AS string) LIKE %:filter%
            )
            """)
    Page<User> findProfessorsNotInClass(@Param("classId") Long classId, @Param("filter") String filter,
            Pageable pageable);

            @Query("""
                SELECT u FROM User u
                WHERE u.id NOT IN (
                    SELECT s.id FROM ClassModel c JOIN c.students s WHERE c.classId = :classId
                )
                AND u.id NOT IN (
                    SELECT p.id FROM ClassModel c JOIN c.professors p WHERE c.classId = :classId
                )
                AND NOT (
                    'COORDINADOR' MEMBER OF u.roles AND SIZE(u.roles) = 1
                )
                AND (
                    'ESTUDIANTE' MEMBER OF u.roles AND SIZE(u.roles) = 1
                )
                AND (
                    :filter IS NULL OR :filter = '' OR
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR
                    CAST(u.institutionalId AS string) LIKE %:filter%
                )
                """)
        Page<User> findStudentsNotInClass(@Param("classId") Long classId, @Param("filter") String filter,
                Pageable pageable);

            

            @Query("SELECT c FROM ClassModel c WHERE c.course = :course AND (LOWER(c.period) LIKE LOWER(CONCAT('%', :period, '%')))")
            List<ClassModel> findClassesByCourseId(Course course, @Param("period") String period);


            @Query("SELECT c FROM ClassModel c WHERE c.course = :course AND (LOWER(c.period) LIKE LOWER(CONCAT('%', :period, '%')) OR CAST(c.javerianaId AS string) LIKE %:filter% OR LOWER(c.period) LIKE LOWER(CONCAT('%', :filter, '%')))")
            List<ClassModel> findClassesByCourseIdAndNameContaining(Course course, @Param("filter") String filter,  @Param("period") String period);

            @Query("SELECT c FROM ClassModel c LEFT JOIN c.professors p WHERE c.course = :course AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR p.institutionalId LIKE CONCAT('%', :filter, '%')  OR LOWER(c.period) LIKE LOWER(CONCAT('%', :filter, '%')))")
            List<ClassModel> findClassesByCourseByProfessorContaining(Course course, @Param("filter") String filter, @Param("period") String period);
        

}
