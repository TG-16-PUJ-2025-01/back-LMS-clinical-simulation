package co.edu.javeriana.lms.booking.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public Page<Room> searchRooms(String keyword, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return roomRepository.findByNameContaining(keyword, pageable);
    }

    private String formatRoomName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public Room save(Room room) {
        // Format the room name
        String formattedName = formatRoomName(room.getName());

        // Search for the room name in the database
        // If it does not exist, create it, if it does, return error
        if (roomRepository.findByName(formattedName) != null) {
            throw new DataIntegrityViolationException("El nombre de la sala ya existe");
        }

        // Search for the room type in the database
        // If it does not exist, create it
        RoomType type = roomTypeRepository.findByName(room.getType().getName());

        if (type == null) {
            type = roomTypeRepository.save(room.getType());
        }

        room.setType(type);
        room.setName(formattedName); // Save the formatted room name

        return roomRepository.save(room);
    }

    public Room update(Room room) {
        // Format the room name
        String formattedName = formatRoomName(room.getName());

        // Search if the room already exists by ID
        Optional<Room> existingRoom = roomRepository.findById(room.getId());

        // If the room exists, proceed with the update logic
        if (existingRoom.isPresent()) {
            Room roomToUpdate = existingRoom.get();

            // Check if the name changed and if the new name already exists
            if (!roomToUpdate.getName().equals(formattedName) && roomRepository.findByName(formattedName) != null) {
                throw new DataIntegrityViolationException("El nombre de la sala ya existe");
            }

            // Check if the room type changed
            RoomType newType = roomTypeRepository.findByName(room.getType().getName());
            if (newType == null) {
                newType = roomTypeRepository.save(room.getType()); // create the new type
            }

            // Update the values
            roomToUpdate.setName(formattedName);
            roomToUpdate.setType(newType);
            roomToUpdate.setCapacity(room.getCapacity());

            return roomRepository.save(roomToUpdate);
        }

        // If the room does not exist, proceed with the creation logic
        if (roomRepository.findByName(formattedName) != null) {
            throw new DataIntegrityViolationException("El nombre de la sala ya existe");
        }

        RoomType type = roomTypeRepository.findByName(room.getType().getName());
        if (type == null) {
            type = roomTypeRepository.save(room.getType());
        }

        room.setType(type);
        room.setName(formattedName); // Save the formatted room name
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
        // Check if the room type trying to be deleted is the last one
        Room room = roomRepository.findById(id).orElse(null);

        if (room != null) {
            RoomType type = room.getType();
            roomRepository.deleteById(id);

            // If it is the last one, delete the room type
            if (roomRepository.countByType(type) == 0) {
                roomTypeRepository.delete(type);
            }
        }
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
