package co.edu.javeriana.lms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;

@Service
public class RoomTypeService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public RoomType save(RoomType type) {
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
