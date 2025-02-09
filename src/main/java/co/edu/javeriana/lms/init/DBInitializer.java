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
        Simulation simulation3 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable1.mp4",
                VideoStatus.UNAVAILABLE, new Date());
        Simulation simulation4 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable2.mp4",
                VideoStatus.UNAVAILABLE, new Date());
        Simulation simulation5 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable3.mp4",
                VideoStatus.UNAVAILABLE, new Date());
        Simulation simulation6 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable4.mp4",
                VideoStatus.UNAVAILABLE, new Date());
        Simulation simulation7 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable5.mp4",
                VideoStatus.UNAVAILABLE, new Date());
        Simulation simulation8 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable6.mp4",
                VideoStatus.UNAVAILABLE, new Date());
        simulationRepository.save(simulation1);
        simulationRepository.save(simulation2);
        simulationRepository.save(simulation3);
        simulationRepository.save(simulation4);
        simulationRepository.save(simulation5);
        simulationRepository.save(simulation6);
        simulationRepository.save(simulation7);
        simulationRepository.save(simulation8);
    }

}
