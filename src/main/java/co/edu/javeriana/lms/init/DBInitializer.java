package co.edu.javeriana.lms.init;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.models.GradeStatus;
import co.edu.javeriana.lms.models.Simulation;
import co.edu.javeriana.lms.models.Video;
import co.edu.javeriana.lms.repositories.SimulationRepository;

@Component
public class DBInitializer implements CommandLineRunner {

        @Autowired
        private SimulationRepository simulationRepository;

        @Override
        public void run(String... args) throws Exception {
                Simulation simulation1 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2023-02-28"));
                simulation1.setVideo(new Video("javatechie.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2023-01-31"),
                                new Date(), 62L, 8.3));

                Simulation simulation2 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"));
                simulation2.setVideo(new Video("10350-224234500_small.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 600L, 31.2));

                Simulation simulation3 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"));
                Video video3 = new Video("unavailable1.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 210L, 300.0);
                video3.setAvailable(false);
                simulation3.setVideo(video3);

                Simulation simulation4 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"));
                Video video4 = new Video("unavailable2.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 450L, 420.0);
                video4.setAvailable(false);
                simulation4.setVideo(video4);

                Simulation simulation5 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"));
                Video video5 = new Video("unavailable3.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 600L, 500.0);
                video5.setAvailable(false);
                simulation5.setVideo(video5);

                Simulation simulation6 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"));
                Video video6 = new Video("unavailable4.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 780L, 780.0);
                video6.setAvailable(false);
                simulation6.setVideo(video6);

                Simulation simulation7 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"));
                Video video7 = new Video("unavailable5.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 6000L, 6000.0);
                video7.setAvailable(false);
                simulation7.setVideo(video7);

                Simulation simulation8 = new Simulation(
                                GradeStatus.PENDING, new SimpleDateFormat("yyyy-MM-dd").parse("2021-02-28"));
                Video video8 = new Video("unavailable6.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 620L, 500.0);
                video8.setAvailable(false);
                simulation8.setVideo(video8);

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
