package co.edu.javeriana.lms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.models.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    RoomType findByName(String name);
}
