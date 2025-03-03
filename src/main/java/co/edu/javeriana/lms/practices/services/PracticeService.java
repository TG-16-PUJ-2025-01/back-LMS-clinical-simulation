package co.edu.javeriana.lms.practices.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PracticeService {

    @Autowired
    private PracticeRepository practiceRepository;

    public Page<Practice> findAll(String keyword, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOder);
        return practiceRepository.findByNameContaining(keyword, pageable);
    }

    public Optional<Practice> findById(Long id) {
        return practiceRepository.findById(id);
    }

    public Practice save(Practice practice) {
        return practiceRepository.save(practice);
    }

    public void deleteById(Long id) {
        practiceRepository.deleteById(id);
    }

    public Practice update(Practice practice) {
        return practiceRepository.save(practice);
    }
}
