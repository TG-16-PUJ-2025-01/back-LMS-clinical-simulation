package co.edu.javeriana.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.models.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
}
