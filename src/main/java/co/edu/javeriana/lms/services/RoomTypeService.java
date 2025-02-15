package co.edu.javeriana.lms.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.RoomType;
import co.edu.javeriana.lms.repositories.RoomTypeRepository;

@Service
public class RoomTypeService implements CrudService<RoomType, Long> {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Override
    public RoomType save(RoomType type) {
        return roomTypeRepository.save(type);
    }

    @Override
    public Optional<RoomType> findById(Long id) {
        return roomTypeRepository.findById(id);
    }

    @Override
    public List<RoomType> findAll(Integer page, Integer size) {
        return roomTypeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        roomTypeRepository.deleteById(id);
    }

}
