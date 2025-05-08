package co.edu.javeriana.lms.config.data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;

@Component
@Profile("prod")
public class DBInitializerProd implements CommandLineRunner {

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private RoomTypeRepository roomTypeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CourseRepository courseRepository;

	@Override
	public void run(String... args) throws Exception {
		insertRoomsAndTypes();
		createUsers();
		insertCourses();
	}

	private void insertRoomsAndTypes() {
		List<RoomType> roomTypes = Arrays.asList(
				RoomType.builder().name("Consultorio").build(),
				RoomType.builder().name("Cuidado crítico intensivo").build(),
				RoomType.builder().name("Cuidado crítico urgencias").build(),
				RoomType.builder().name("Cuidado crítico ginecobstetricia").build(),
				RoomType.builder().name("Hospitalización").build(),
				RoomType.builder().name("Observación").build(),
				RoomType.builder().name("Cirugía").build(),
				RoomType.builder().name("Laparoscopia").build());

		roomTypeRepository.saveAll(roomTypes);

		List<Room> rooms = Arrays.asList(
				Room.builder().name("Consultorio 1").type(roomTypes.get(0)).capacity(11).ip("10.197.140.234").build(),
				Room.builder().name("Consultorio 2").type(roomTypes.get(0)).capacity(11).ip("10.197.140.235").build(),
				Room.builder().name("Consultorio 3").type(roomTypes.get(0)).capacity(11).ip("10.197.140.236").build(),

				Room.builder().name("Cuidado crítico 1A").type(roomTypes.get(1)).capacity(11).ip("10.197.140.239")
						.build(),
				Room.builder().name("Cuidado crítico 1B").type(roomTypes.get(1)).capacity(11).ip("10.197.140.237")
						.build(),

				Room.builder().name("Cuidado crítico 2A").type(roomTypes.get(2)).capacity(11).ip("10.197.140.240")
						.build(),
				Room.builder().name("Cuidado crítico 2B").type(roomTypes.get(2)).capacity(11).ip("10.197.140.238")
						.build(),

				Room.builder().name("Cuidado crítico 3A").type(roomTypes.get(3)).capacity(11).ip("10.197.140.242")
						.build(),
				Room.builder().name("Cuidado crítico 3B").type(roomTypes.get(3)).capacity(11).ip("10.197.140.238")
						.build(),

				Room.builder().name("Hospitalización 1").type(roomTypes.get(4)).capacity(11).ip("10.197.140.128")
						.build(),
				Room.builder().name("Hospitalización 2").type(roomTypes.get(4)).capacity(11).ip("10.197.140.133")
						.build(),
				Room.builder().name("Hospitalización 3").type(roomTypes.get(4)).capacity(11).ip("10.197.140.132")
						.build(),

				Room.builder().name("Observación 1").type(roomTypes.get(5)).capacity(11).ip("10.197.140.211").build(),
				Room.builder().name("Observación 2").type(roomTypes.get(5)).capacity(11).ip("10.197.140.206").build(),
				Room.builder().name("Observación 3").type(roomTypes.get(5)).capacity(11).ip("10.197.140.209").build(),

				Room.builder().name("Cirugía 1").type(roomTypes.get(6)).capacity(11).ip("10.197.140.207").build(),
				Room.builder().name("Cirugía 2").type(roomTypes.get(6)).capacity(11).ip("10.197.140.210").build(),

				Room.builder().name("Laparoscopia").type(roomTypes.get(7)).capacity(11).ip("10.197.140.208").build());

		roomRepository.saveAll(rooms);
	}

	private void createUsers() {

		List<User> users = Arrays.asList(
				// Profesores
				User.builder().email("profesor@gmail.com").password(passwordEncoder.encode("profesor"))
						.name("María").lastName("López").institutionalId("00000010001")
						.roles(Set.of(Role.PROFESOR)).build(),

				// Coordinadores
				User.builder().email("coordinador@gmail.com").password(passwordEncoder.encode("coordinador"))
						.name("Laura").lastName("Martínez").institutionalId("00000020001")
						.roles(Set.of(Role.COORDINADOR)).build(),

				// Administradores
				User.builder().email("admin@gmail.com").password(passwordEncoder.encode("admin"))
						.name("Andrés").lastName("García").institutionalId("00000040001")
						.roles(Set.of(Role.ADMIN)).build(),

				// SuperAdmin (todos los roles)
				User.builder().email("superadmin@gmail.com").password(passwordEncoder.encode("superadmin"))
						.name("Sofía").lastName("Admin").institutionalId("00000090001")
						.roles(Set.of(Role.ADMIN, Role.COORDINADOR, Role.PROFESOR, Role.ESTUDIANTE)).build(),

				// Estudiantes
				User.builder().email("estudiante@gmail.com").password(passwordEncoder.encode("estudiante"))
						.name("Camilo").lastName("Mendoza").institutionalId("00000050001")
						.roles(Set.of(Role.ESTUDIANTE)).build());

		// Guardar todos los usuarios
		userRepository.saveAll(users);
	}

	private void insertCourses() {

		List<User> allCoordinators = userRepository.findAllCoordinators();

		List<Course> courses = List.of(
				Course.builder()
						.name("Semiología Clínica")
						.javerianaId(100001L)
						.coordinator(allCoordinators.get(0))
						.faculty("medicina")
						.department("medicina interna")
						.program("pregrado")
						.semester(1)
						.build(),

				Course.builder()
						.name("Farmacología General")
						.javerianaId(100002L)
						.coordinator(allCoordinators.get(1))
						.faculty("medicina")
						.department("farmacología")
						.program("pregrado")
						.semester(2)
						.build(),

				Course.builder()
						.name("Cuidados de Enfermería en el Adulto")
						.javerianaId(100003L)
						.coordinator(allCoordinators.get(2))
						.faculty("enfermeria")
						.department("cuidados intensivos")
						.program("pregrado")
						.semester(3)
						.build(),

				Course.builder()
						.name("Fisiopatología Clínica")
						.javerianaId(100004L)
						.coordinator(allCoordinators.get(3))
						.faculty("medicina")
						.department("medicina interna")
						.program("maestria")
						.semester(4)
						.build(),

				Course.builder()
						.name("Terapia Intravenosa y Manejo de Vía Aérea")
						.javerianaId(100005L)
						.coordinator(allCoordinators.get(4))
						.faculty("enfermeria")
						.department("emergencias")
						.program("especialización")
						.semester(5)
						.build(),

				Course.builder()
						.name("Simulación Clínica Avanzada")
						.javerianaId(100006L)
						.coordinator(allCoordinators.get(0))
						.faculty("medicina")
						.department("medicina crítica")
						.program("doctorado")
						.semester(6)
						.build(),

				Course.builder()
						.name("Atención Primaria en Salud")
						.javerianaId(100007L)
						.coordinator(allCoordinators.get(0))
						.faculty("medicina")
						.department("medicina familiar")
						.program("pregrado")
						.semester(7)
						.build(),

				Course.builder()
						.name("Ética y Humanismo Médico")
						.javerianaId(100008L)
						.coordinator(userRepository.findById(3L).get())
						.faculty("medicina")
						.department("ética médica")
						.program("pregrado")
						.semester(8)
						.build());

		courseRepository.saveAll(courses);
	}

}