package co.edu.javeriana.lms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.models.RoomType;
import co.edu.javeriana.lms.repositories.RoomRepository;
import co.edu.javeriana.lms.repositories.RoomTypeRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public Room save(Room room) {
        // Search for the room name in the database
        // If it does not exist, create it, if it does, return error
        if (roomRepository.findByName(room.getName()) != null) {
            throw new IllegalArgumentException("Room name already exists");
        }

        // Search for the room type in the database
        // If it does not exist, create it
        RoomType type = roomTypeRepository.findByName(room.getType().getName());

        if (type == null) {
            type = roomTypeRepository.save(room.getType());
        }

        room.setType(type);

        return roomRepository.save(room);
    }

    public Room update(Room room) {
        // Search if the room already exists by ID
        Optional<Room> existingRoom = roomRepository.findById(room.getId());

        // If the room exists, proceed with the update logic
        if (existingRoom.isPresent()) {
            Room roomToUpdate = existingRoom.get();

            // Check if the name changed and if the new name already exists
            if (!roomToUpdate.getName().equals(room.getName()) && roomRepository.findByName(room.getName()) != null) {
                throw new IllegalArgumentException("Room name already exists");
            }

            // Check if the room type changed
            RoomType newType = roomTypeRepository.findByName(room.getType().getName());
            if (newType == null) {
                newType = roomTypeRepository.save(room.getType()); // create the new type
            }

            // Update the values
            roomToUpdate.setName(room.getName());
            roomToUpdate.setType(newType);

            return roomRepository.save(roomToUpdate);
        }

        // If the room does not exist, proceed with the creation logic
        if (roomRepository.findByName(room.getName()) != null) {
            throw new IllegalArgumentException("Room name already exists");
        }

        RoomType type = roomTypeRepository.findByName(room.getType().getName());
        if (type == null) {
            type = roomTypeRepository.save(room.getType());
        }

        room.setType(type);
        return roomRepository.save(room);
    }


    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public List<Room> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.findAll(pageable).getContent();
    }

    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        roomRepository.deleteById(id);
    }

    public Room findByName(String name) {
        return roomRepository.findByName(name);
    }

    public boolean existsByName(String name) {
        return roomRepository.findByName(name) != null;
    }

    public RoomType findRoomTypeByName(String name) {
        return roomTypeRepository.findByName(name);
    }

    public RoomType saveRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    public List<RoomType> findAllTypes() {
        return roomTypeRepository.findAll();
    }

    public long countByType(RoomType type) {
        return roomRepository.countByType(type);
    }
}
