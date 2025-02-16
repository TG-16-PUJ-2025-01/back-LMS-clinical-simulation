package co.edu.javeriana.lms.services;

import java.util.List;

public interface CrudService<T, ID> {
    T save(T entity);
    T findById(ID id);
    List<T> findAll(Integer page, Integer size);
    void deleteById(ID id);
}
