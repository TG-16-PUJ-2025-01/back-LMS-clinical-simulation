package co.edu.javeriana.lms.init;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.models.GradeStatus;
import co.edu.javeriana.lms.models.Simulation;
import co.edu.javeriana.lms.models.VideoStatus;
import co.edu.javeriana.lms.repositories.SimulationRepository;

@Component
public class DBInitializer implements CommandLineRunner {

    @Autowired
    private SimulationRepository simulationRepository;

    @Override
    public void run(String... args) throws Exception {
        Simulation simulation1 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2023-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2023-02-28"), "javatechie.mp4",
                VideoStatus.AVAILABLE, new Date());
        Simulation simulation2 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable.mp4",
                VideoStatus.UNAVAILABLE, new Date());
        simulationRepository.save(simulation1);
        simulationRepository.save(simulation2);
    }

}
