package co.edu.javeriana.lms.grades.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GradeService {
    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    
}
