package co.edu.javeriana.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LmsClinicalSimulationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsClinicalSimulationApplication.class, args);
	}
}
