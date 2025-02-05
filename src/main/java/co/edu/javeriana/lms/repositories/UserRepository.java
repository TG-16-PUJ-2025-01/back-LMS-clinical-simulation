package co.edu.javeriana.lms.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.javeriana.lms.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
