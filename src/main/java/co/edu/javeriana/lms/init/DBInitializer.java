package co.edu.javeriana.lms.init;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.models.Video;
import co.edu.javeriana.lms.repositories.VideoRepository;

@Component
public class DBInitializer implements CommandLineRunner {

        @Autowired
        private VideoRepository videoRepository;

        @Override
        public void run(String... args) throws Exception {
                Video video1 = new Video("javatechie.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2023-01-31"),
                                new Date(), 62L, 8.3);

                Video video2 = new Video("10350-224234500_small.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 600L, 31.2);

                Video video3 = new Video("unavailable1.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 210L, 300.0);
                video3.setAvailable(false);

                Video video4 = new Video("unavailable2.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 450L, 420.0);
                video4.setAvailable(false);

                Video video5 = new Video("unavailable3.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 600L, 500.0);
                video5.setAvailable(false);

                Video video6 = new Video("unavailable4.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 780L, 780.0);
                video6.setAvailable(false);

                Video video7 = new Video("unavailable5.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 6000L, 6000.0);
                video7.setAvailable(false);

                Video video8 = new Video("unavailable6.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 620L, 500.0);
                video8.setAvailable(false);

                videoRepository.save(video1);
                videoRepository.save(video2);
                videoRepository.save(video3);
                videoRepository.save(video4);
                videoRepository.save(video5);
                videoRepository.save(video6);
                videoRepository.save(video7);
                videoRepository.save(video8);

        }

}
