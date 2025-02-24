package co.edu.javeriana.lms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(TRANSLATE(u.name, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
        OR LOWER(TRANSLATE(u.lastName, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU')) LIKE LOWER(CONCAT('%', TRANSLATE(:filter, 'áéíóúÁÉÍÓÚ', 'aeiouAEIOU'), '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :filter, '%'))
        OR CAST(u.institutionalId AS string) LIKE %:filter%
    """)
    Page<User> findAllFiltered(@Param("filter") String filter, Pageable pageable);
    // Buscar todos los coordinadores
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = 'COORDINADOR'")
    List<User> findAllCoordinators();

    // Buscar todos los profesores
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = 'PROFESOR'")
    List<User> findAllProfessors();
}
