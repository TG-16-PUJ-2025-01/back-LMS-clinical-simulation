package co.edu.javeriana.lms.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
    Boolean existsByEmail(String email);

    // Buscar todos los coordinadores
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = 'COORDINADOR'")
    List<User> findAllCoordinators();

    // Buscar todos los profesores
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = 'PROFESOR'")
    List<User> findAllProfessors();
}
