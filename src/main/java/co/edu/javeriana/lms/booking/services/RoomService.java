package co.edu.javeriana.lms.booking.services;

import java.util.List;

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
import jakarta.persistence.EntityNotFoundException;

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
        String formattedName = formatRoomName(room.getName());

        if (roomRepository.findByName(formattedName) != null) {
            throw new DataIntegrityViolationException("El nombre de la sala ya existe");
        }

        RoomType type = roomTypeRepository.findById(room.getType().getId())
                .orElseThrow(() -> new EntityNotFoundException("Room type not found with id: " + room.getType().getId()));

        room.setName(formattedName);
        room.setType(type);

        return roomRepository.save(room);
    }

    public Room update(Long id, Room room) {
        String formattedName = formatRoomName(room.getName());

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + id));

        if (!existingRoom.getName().equals(formattedName) && roomRepository.findByName(formattedName) != null) {
            throw new DataIntegrityViolationException("El nombre de la sala ya existe");
        }

        RoomType type = roomTypeRepository.findById(room.getType().getId())
                .orElseThrow(() -> new EntityNotFoundException("Room type not found with id: " + room.getType().getId()));

        existingRoom.setName(formattedName);
        existingRoom.setCapacity(room.getCapacity());
        existingRoom.setIp(room.getIp());
        existingRoom.setType(type);

        return roomRepository.save(existingRoom);
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + id));
    }

    public List<Room> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.findAll(pageable).getContent();
    }

    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + id));

        // Check if the room type trying to be deleted is the last one
        RoomType type = room.getType();
        roomRepository.deleteById(id);

        // If it is the last one, delete the room type
        if (roomRepository.countByType(type) == 0) {
            roomTypeRepository.delete(type);
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