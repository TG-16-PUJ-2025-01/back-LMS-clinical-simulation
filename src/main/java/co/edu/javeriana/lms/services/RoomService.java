package co.edu.javeriana.lms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.models.RoomType;
import co.edu.javeriana.lms.repositories.RoomRepository;
import co.edu.javeriana.lms.repositories.RoomTypeRepository;

@Service
public class RoomService implements CrudService<Room, Long> {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Override
    public Room save(Room room) {
        // Search for the room type in the database
        // If it does not exist, create it
        RoomType type = roomTypeRepository.findByName(room.getType().getName());

        if (type == null) {
            type = roomTypeRepository.save(room.getType());
        }

        room.setType(type);

        return roomRepository.save(room);
    }

    @Override
    public Room findById(Long id) {
        return roomRepository.findById(id).get();
    }

    @Override
    public List<Room> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.findAll(pageable).getContent();
    }

    @Override
    public void deleteById(Long id) {
        roomRepository.deleteById(id);
    }

    
    
}
