package co.edu.javeriana.lms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.repository.RoomRepository;

@Service
public class RoomService implements CrudService<Room, Long> {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room save(Room entity) {
        return roomRepository.save(entity);
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        roomRepository.deleteById(id);
    }

    
    
}
