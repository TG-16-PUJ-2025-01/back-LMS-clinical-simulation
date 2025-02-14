package co.edu.javeriana.lms.services;

import java.util.List;
import java.util.Optional;

import co.edu.javeriana.lms.models.ClassModel;

public interface CrudService<T, ID> {
    T save(T entity);
    T findById(ID id);
    List<T> findAll(Integer page, Integer size);
    void deleteById(ID id);
}
