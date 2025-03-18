package co.edu.javeriana.lms.practices.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.models.Practice;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN false ELSE true END FROM Simulation s WHERE s.room = :room AND s.startDateTime < :endDateTime AND s.endDateTime > :startDateTime")
    Boolean isRoomAvailable(@Param("room") Room room, @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    List<Simulation> findByRoomId(Long roomId);

    Page<Simulation> findByPracticeId(Long practiceId, Pageable pageable);

    List<Simulation> findByRoomIdAndStartDateTimeAfter(Long roomId, LocalDateTime startDateTime);

    List<Simulation> findByPracticeIn(List<Practice> practices);

    List<Simulation> findByPracticeInAndStartDateTimeBetween(
            List<Practice> practices, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
