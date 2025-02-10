package co.edu.javeriana.lms.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.models.PasswordResetToken;
import jakarta.transaction.Transactional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByToken(String token);    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expirationDate < :now")
    void deleteAllExpiredTokens(@Param("now") LocalDateTime now);

}
