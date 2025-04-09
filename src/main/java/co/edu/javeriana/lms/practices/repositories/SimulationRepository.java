package co.edu.javeriana.lms.practices.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.subjects.models.ClassModel;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

        @Query("SELECT CASE WHEN COUNT(s) > 0 THEN false ELSE true END " +
                        "FROM Simulation s " +
                        "JOIN s.rooms r " +
                        "WHERE r = :room " +
                        "AND s.startDateTime < :endDateTime " +
                        "AND s.endDateTime > :startDateTime")
        Boolean isRoomAvailable(@Param("room") Room room,
                        @Param("startDateTime") Date startDateTime,
                        @Param("endDateTime") Date endDateTime);

        Page<Simulation> findByPracticeId(Long practiceId, Pageable pageable);

        List<Simulation> findByStartDateTimeAfter(Date startDateTime);

        List<Simulation> findByStartDateTimeBetween(Date startDateTime, Date endDateTime);

        List<Simulation> findByPracticeIn(List<Practice> practices);

        @Query("SELECT MAX(s.groupNumber) FROM Simulation s WHERE s.practice.id = :practiceId")
        Integer findMaxGroupNumberByPracticeId(@Param("practiceId") Long practiceId);

        Page<Simulation> findByPracticeIdAndGroupNumberContaining(Long practiceId, String groupNumber,
                        Pageable pageable);

        Page<Simulation> findByPracticeIdAndGroupNumber(Long practiceId, Integer groupNumber, Pageable pageable);

        List<Simulation> findByPracticeInAndStartDateTimeBetween(
                        List<Practice> practices,
                        Date startDate,
                        Date endDate);

        List<Simulation> findByUsers_IdAndStartDateTimeBetween(
                        Long userId,
                        Date startDate,
                        Date endDate);

        List<Simulation> findAllByPractice_ClassModel(ClassModel classModel);
}
