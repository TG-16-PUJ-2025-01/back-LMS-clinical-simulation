package co.edu.javeriana.lms.init;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.models.GradeStatus;
import co.edu.javeriana.lms.models.Simulation;
import co.edu.javeriana.lms.repositories.SimulationRepository;

@Component
public class DBInitializer implements CommandLineRunner {

        @Autowired
        private VideoRepository videoRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

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
                Video video9 = new Video("unavailable7.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 620L, 500.0);
                video9.setAvailable(false);
                Video video10 = new Video("unavailable8.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 620L, 500.0);
                video10.setAvailable(false);
                Video video11 = new Video("unavailable9.mp4",
                                new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-31"),
                                new Date(), 620L, 500.0);
                video11.setAvailable(false);

                videoRepository.save(video1);
                videoRepository.save(video2);
                videoRepository.save(video3);
                videoRepository.save(video4);
                videoRepository.save(video5);
                videoRepository.save(video6);
                videoRepository.save(video7);
                videoRepository.save(video8);
                videoRepository.save(video9);
                videoRepository.save(video10);
                videoRepository.save(video11);

            
        insertRoomsAndTypes();
    }

    public void insertRoomsAndTypes() {
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");
        roomTypeRepository.save(roomType1);

        RoomType roomType2 = new RoomType();
        roomType2.setName("Urgencias");
        roomTypeRepository.save(roomType2);

        RoomType roomType3 = new RoomType();
        roomType3.setName("Consulta Externa");
        roomTypeRepository.save(roomType3);

        RoomType roomType4 = new RoomType();
        roomType4.setName("Hospitalizacion");
        roomTypeRepository.save(roomType4);

        RoomType roomType5 = new RoomType();
        roomType5.setName("Laboratorio");
        roomTypeRepository.save(roomType5);

        Room room1 = new Room();
        room1.setName("Sala1");
        room1.setType(roomType1);
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setName("Sala2");
        room2.setType(roomType1);
        roomRepository.save(room2);

        Room room3 = new Room();
        room3.setName("Sala3");
        room3.setType(roomType2);
        roomRepository.save(room3);

        Room room4 = new Room();
        room4.setName("Sala4");
        room4.setType(roomType2);
        roomRepository.save(room4);

        Room room5 = new Room();
        room5.setName("Sala5");
        room5.setType(roomType3);
        roomRepository.save(room5);

        Room room6 = new Room();
        room6.setName("Sala6");
        room6.setType(roomType3);
        roomRepository.save(room6);

        Room room7 = new Room();
        room7.setName("Sala7");
        room7.setType(roomType4);
        roomRepository.save(room7);

        Room room8 = new Room();
        room8.setName("Sala8");
        room8.setType(roomType4);
        roomRepository.save(room8);

        Room room9 = new Room();
        room9.setName("Sala9");
        room9.setType(roomType5);
        roomRepository.save(room9);

        Room room10 = new Room();
        room10.setName("Sala10");
        room10.setType(roomType5);
        roomRepository.save(room10);
    }

}
