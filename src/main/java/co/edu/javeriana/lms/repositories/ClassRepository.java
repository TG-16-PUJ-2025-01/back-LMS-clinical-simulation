package co.edu.javeriana.lms.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import co.edu.javeriana.lms.models.ClassModel;
import co.edu.javeriana.lms.models.User;

@Repository
public interface ClassRepository extends JpaRepository<ClassModel, Long> {
    @Query("""
                SELECT c FROM ClassModel c
                WHERE CAST(c.javerianaId AS string) LIKE %:filter%
            """)
    Page<ClassModel> findByJaverianaId(@Param("filter") String filter, Pageable pageable);

    // obtener todos los miembros de una clase
    @Query("""
                SELECT u FROM User u
                WHERE u.id IN (
                    SELECT s.id FROM ClassModel c JOIN c.students s WHERE c.classId = :classId
                )
                OR u.id IN (
                    SELECT p.id FROM ClassModel c JOIN c.professors p WHERE c.classId = :classId
                )
            """)
    Page<User> findMembers(@Param("classId") Long classId, Pageable pageable);

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
            """)
    Page<User> findUsersNotInClass(@Param("classId") Long classId, Pageable pageable);

}
