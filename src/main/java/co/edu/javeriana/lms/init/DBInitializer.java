package co.edu.javeriana.lms.init;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.models.GradeStatus;
import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.models.RoomType;
import co.edu.javeriana.lms.models.Role;
import co.edu.javeriana.lms.models.Simulation;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.models.VideoStatus;
import co.edu.javeriana.lms.repositories.SimulationRepository;
import co.edu.javeriana.lms.repositories.RoomRepository;
import co.edu.javeriana.lms.repositories.RoomTypeRepository;
import co.edu.javeriana.lms.repositories.UserRepository;

@Component
public class DBInitializer implements CommandLineRunner {

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        Simulation simulation1 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2023-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2023-02-28"), "javatechie.mp4",
                VideoStatus.AVAILABLE, new Date(), 62L, 8.3);
        Simulation simulation2 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "10350-224234500_small.mp4",
                VideoStatus.AVAILABLE, new Date(), 600L, 31.2);
        Simulation simulation3 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable1.mp4",
                VideoStatus.UNAVAILABLE, new Date(), 210L, 300.0);
        Simulation simulation4 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable2.mp4",
                VideoStatus.UNAVAILABLE, new Date(), 450L, 420.0);
        Simulation simulation5 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable3.mp4",
                VideoStatus.UNAVAILABLE, new Date(), 600L, 500.0);
        Simulation simulation6 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable4.mp4",
                VideoStatus.UNAVAILABLE, new Date(), 780L, 780.0);
        Simulation simulation7 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable5.mp4",
                VideoStatus.UNAVAILABLE, new Date(), 6000L, 6000.0);
        Simulation simulation8 = new Simulation(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"), "unavailable6.mp4",
                VideoStatus.UNAVAILABLE, new Date(), 620L, 500.0);
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
