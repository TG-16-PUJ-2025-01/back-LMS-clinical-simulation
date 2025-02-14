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
import co.edu.javeriana.lms.models.Role;
import co.edu.javeriana.lms.models.Simulation;
import co.edu.javeriana.lms.models.User;
import co.edu.javeriana.lms.models.VideoStatus;
import co.edu.javeriana.lms.repositories.SimulationRepository;
import co.edu.javeriana.lms.repositories.UserRepository;

@Component
public class DBInitializer implements CommandLineRunner {

    @Autowired
    private SimulationRepository simulationRepository;

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

        createUsers();

    }


    private void createUsers() {
                //crear usuarios profesores
                User professor1= new User();
                User professor2= new User();
                professor1.setEmail("saristizabal10@gamil.com");
                professor1.setPassword("123456");
                professor1.setName("Santiago");
                professor1.setLastName("Aristizabal");
                professor1.setInstitutionalId(123456);
                professor1.setRoles(Set.of(Role.PROFESOR));
        
                professor2.setEmail("pedro10@gamil.com");
                professor2.setPassword("123456");
                professor2.setName("Pepo");
                professor2.setLastName("Pascal");
                professor2.setInstitutionalId(1256);
                professor2.setRoles(Set.of(Role.PROFESOR));
               
                //crear usuarios coordinadores
                User coord1= new User();
                User coord2= new User();
        
                coord1.setEmail("saabal10@gamil.com");
                coord1.setPassword("123456");
                coord1.setName("Salomon");
                coord1.setLastName("Pira");
                coord1.setInstitutionalId(456);
                coord1.setRoles(Set.of(Role.COORDINADOR));
        
                coord2.setEmail("pucoeocents0@gamil.com");
                coord2.setPassword("13456");
                coord2.setName("Pedro");
                coord2.setLastName("Puentes");
                coord2.setInstitutionalId(56);
                coord2.setRoles(Set.of(Role.COORDINADOR));
        
                //crear usuarios con ambos tags
                User both1= new User();
                User both2= new User();
        
                both1.setEmail("saaal10@gamil.com");
                both1.setPassword("123456");
                both1.setName("Salomon ndienid");
                both1.setLastName("Pira");
                both1.setInstitutionalId(45996);
                both1.setRoles(new HashSet<>(Arrays.asList(Role.PROFESOR, Role.COORDINADOR)));
        
        
                both2.setEmail("puenjnjnts0@gamil.com");
                both2.setPassword("13456");
                both2.setName("Pedro idjei");
                both2.setLastName("Puentes");
                both2.setInstitutionalId(5690);
                both2.setRoles(new HashSet<>(Arrays.asList(Role.PROFESOR, Role.COORDINADOR)));
        
                userRepository.save(professor1);
                userRepository.save(professor2);
                userRepository.save(coord1);
                userRepository.save(coord2);
                userRepository.save(both1);
                userRepository.save(both2);
    }
}

