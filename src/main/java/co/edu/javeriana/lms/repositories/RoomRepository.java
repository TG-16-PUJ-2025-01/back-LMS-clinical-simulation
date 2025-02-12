package co.edu.javeriana.lms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import co.edu.javeriana.lms.models.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByName(String name);

    Page<Room> findAll(Pageable pageable);
    
}
