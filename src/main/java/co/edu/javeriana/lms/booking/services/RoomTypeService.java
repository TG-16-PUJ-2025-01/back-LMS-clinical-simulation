package co.edu.javeriana.lms.booking.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;

@Service
public class RoomTypeService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public RoomType save(RoomType type) {
        if (type.getName().isEmpty()) {
            throw new DataIntegrityViolationException("El nombre del tipo de sala no puede estar vacio");
        }
        if (roomTypeRepository.findByName(type.getName()) != null) {
            throw new DataIntegrityViolationException("El nombre del tipo de sala ya existe: " + type.getName());
        }
        return roomTypeRepository.save(type);
    }

    public Optional<RoomType> findById(Long id) {
        return roomTypeRepository.findById(id);
    }

    public List<RoomType> findAll(Integer page, Integer size) {
        return roomTypeRepository.findAll();
    }

    public void deleteById(Long id) {
        roomTypeRepository.deleteById(id);
    }

}
