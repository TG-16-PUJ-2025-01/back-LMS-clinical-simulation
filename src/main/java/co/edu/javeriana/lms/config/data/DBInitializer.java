package co.edu.javeriana.lms.config.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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
import co.edu.javeriana.lms.grades.models.GradeStatus;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.PracticeType;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import co.edu.javeriana.lms.subjects.repositories.CourseRepository;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;

@Component
@Profile({ "dev", "test" })
public class DBInitializer implements CommandLineRunner {

	@Autowired
	private VideoRepository videoRepository;

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

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private PracticeRepository practiceRepository;

	@Autowired
	private SimulationRepository simulationRepository;

	@Override
	public void run(String... args) throws Exception {
		insertRoomsAndTypes();
		createUsers();
		insertCoursesClassesAndStudents();
		insertPractices();
		insertSimulations();
		insertRubricTemplates();
		insertSimulationsVideosAndComments();
	}

	private void insertRoomsAndTypes() {
		List<RoomType> roomTypes = Arrays.asList(
				RoomType.builder().name("Cuidado crítico intensivo").build(),
				RoomType.builder().name("Cuidado crítico urgencias").build(),
				RoomType.builder().name("Cuidado crítico ginecobstetricia").build(),
				RoomType.builder().name("Consultorios").build(),
				RoomType.builder().name("Salas debriefing").build(),
				RoomType.builder().name("Observación").build(),
				RoomType.builder().name("Hospitalización").build(),
				RoomType.builder().name("Microcirugía").build(),
				RoomType.builder().name("Cirugía").build(),
				RoomType.builder().name("Procedimiento y habilidades quirúrgicas").build(),
				RoomType.builder().name("Farmacia").build(),
				RoomType.builder().name("Salones").build());

		roomTypeRepository.saveAll(roomTypes);

		List<Room> rooms = Arrays.asList(
				Room.builder().name("Sala1").type(roomTypes.get(0)).capacity(11).build(),
				Room.builder().name("Sala2").type(roomTypes.get(1)).capacity(14).build(),
				Room.builder().name("Sala3").type(roomTypes.get(2)).capacity(13).build(),
				Room.builder().name("Sala4").type(roomTypes.get(3)).capacity(11).build(),
				Room.builder().name("Sala5").type(roomTypes.get(3)).capacity(1).build(),
				Room.builder().name("Sala6").type(roomTypes.get(3)).capacity(11).build(),
				Room.builder().name("Sala7").type(roomTypes.get(4)).capacity(15).build(),
				Room.builder().name("Sala8").type(roomTypes.get(4)).capacity(11).build(),
				Room.builder().name("Sala9").type(roomTypes.get(4)).capacity(11).build(),
				Room.builder().name("Sala10").type(roomTypes.get(5)).capacity(11).build(),
				Room.builder().name("Sala11").type(roomTypes.get(5)).capacity(11).build(),
				Room.builder().name("Sala12").type(roomTypes.get(5)).capacity(14).build(),
				Room.builder().name("Sala13").type(roomTypes.get(6)).capacity(11).build(),
				Room.builder().name("Sala14").type(roomTypes.get(6)).capacity(11).build(),
				Room.builder().name("Sala15").type(roomTypes.get(6)).capacity(21).build(),
				Room.builder().name("Sala16").type(roomTypes.get(7)).capacity(11).build(),
				Room.builder().name("Sala17").type(roomTypes.get(8)).capacity(13).build(),
				Room.builder().name("Sala18").type(roomTypes.get(9)).capacity(11).build(),
				Room.builder().name("Sala19").type(roomTypes.get(10)).capacity(51).build(),
				Room.builder().name("Sala20").type(roomTypes.get(11)).capacity(11).build(),
				Room.builder().name("Sala21").type(roomTypes.get(11)).capacity(11).build(),
				Room.builder().name("Sala22").type(roomTypes.get(11)).capacity(11).build());

		roomRepository.saveAll(rooms);
	}

	private void createUsers() {
		// crear usuarios profesores
		User professor1 = new User();
		User professor2 = new User();
		professor1.setEmail("saristizabal10@javeriana.edu.co");
		professor1.setPassword(passwordEncoder.encode("123456"));
		professor1.setName("Santiago");
		professor1.setLastName("Aristizabal");
		professor1.setInstitutionalId("123456");
		professor1.setRoles(Set.of(Role.PROFESOR));

		professor2.setEmail("pedro10@javeriana.edu.co");
		professor2.setPassword(passwordEncoder.encode("123456"));
		professor2.setName("Pepo");
		professor2.setLastName("Pascal");
		professor2.setInstitutionalId("1256");
		professor2.setRoles(Set.of(Role.PROFESOR));

		// crear usuarios coordinadores
		User coord1 = new User();
		User coord2 = new User();

		coord1.setEmail("saabal10@javeriana.edu.co");
		coord1.setPassword(passwordEncoder.encode("123456"));
		coord1.setName("Salomon");
		coord1.setLastName("Pira");
		coord1.setInstitutionalId("456");
		coord1.setRoles(Set.of(Role.COORDINADOR));

		coord2.setEmail("pucoeocents0@javeriana.edu.co");
		coord2.setPassword(passwordEncoder.encode("123456"));
		coord2.setName("Pedro");
		coord2.setLastName("Puentes");
		coord2.setInstitutionalId("56");
		coord2.setRoles(Set.of(Role.COORDINADOR));

		// crear usuarios con ambos tags
		User both1 = new User();
		User both2 = new User();

		both1.setEmail("saaal10@javeriana.edu.co");
		both1.setPassword(passwordEncoder.encode("123456"));
		both1.setName("Salomon ndienid");
		both1.setLastName("Pira");
		both1.setInstitutionalId("45996");
		both1.setRoles(new HashSet<>(Arrays.asList(Role.PROFESOR, Role.COORDINADOR)));

		both2.setEmail("puenjnjnts0@javeriana.edu.co");
		both2.setPassword(passwordEncoder.encode("123456"));
		both2.setName("Pedro idjei");
		both2.setLastName("Puentes");
		both2.setInstitutionalId("5690");
		both2.setRoles(new HashSet<>(Arrays.asList(Role.PROFESOR, Role.COORDINADOR)));

		// Admin
		User admin = new User();
		admin.setEmail("andresgarciam@javeriana.edu.co");
		admin.setPassword(passwordEncoder.encode("Peter2010?"));
		admin.setName("Andres");
		admin.setLastName("Garcia");
		admin.setInstitutionalId("98675");
		admin.setRoles(Set.of(Role.ADMIN));

		User admin2 = new User();
		admin2.setEmail("admin@gmail.com");
		admin2.setPassword(passwordEncoder.encode("admin"));
		admin2.setName("admin");
		admin2.setLastName("admin");
		admin2.setInstitutionalId("111222");
		admin2.setRoles(Set.of(Role.ADMIN));

		User superAdmin = new User();
		superAdmin.setEmail("superadmin@gmail.com");
		superAdmin.setPassword(passwordEncoder.encode("superadmin"));
		superAdmin.setName("super");
		superAdmin.setLastName("admin");
		superAdmin.setInstitutionalId("111223");
		superAdmin.setRoles(Set.of(Role.ADMIN, Role.COORDINADOR, Role.PROFESOR, Role.ESTUDIANTE));

		User student = new User();
		student.setEmail("sopita@javeriana.edu.co");
		student.setPassword(passwordEncoder.encode("123"));
		student.setName("Sophie");
		student.setLastName("Aristi");
		student.setInstitutionalId("19281");
		student.setRoles(Set.of(Role.ESTUDIANTE));

		User student2 = new User();
		student2.setEmail("estudiante@gmail.com");
		student2.setPassword(passwordEncoder.encode("estudiante"));
		student2.setName("estudiante");
		student2.setLastName("estudiante");
		student2.setInstitutionalId("111224");
		student2.setRoles(Set.of(Role.ESTUDIANTE));

		User student3 = new User();
		student3.setEmail("estudiante3@gmail.com");
		student3.setPassword(passwordEncoder.encode("estudiante2"));
		student3.setName("estudiante3");
		student3.setLastName("estudiante3");
		student3.setInstitutionalId("911225");
		student3.setRoles(Set.of(Role.ESTUDIANTE));

		User student4 = new User();
		student4.setEmail("estudiante4@gmail.com");
		student4.setPassword(passwordEncoder.encode("estudiante4"));
		student4.setName("estudiante4");
		student4.setLastName("estudiante4");
		student4.setInstitutionalId("911226");
		student4.setRoles(Set.of(Role.ESTUDIANTE));
		
		User student5 = new User();
		student5.setEmail("estudiante5@gmail.com");
		student5.setPassword(passwordEncoder.encode("estudiante5"));
		student5.setName("estudiante5");
		student5.setLastName("estudiante5");
		student5.setInstitutionalId("911227");
		student5.setRoles(Set.of(Role.ESTUDIANTE));


		User profesor3 = new User();
		profesor3.setEmail("profesor@gmail.com");
		profesor3.setPassword(passwordEncoder.encode("profesor"));
		profesor3.setName("profesor");
		profesor3.setLastName("profesor");
		profesor3.setInstitutionalId("111225");
		profesor3.setRoles(Set.of(Role.PROFESOR));

		User coordinator = new User();
		coordinator.setEmail("coordinador@gmail.com");
		coordinator.setPassword(passwordEncoder.encode("coordinador"));
		coordinator.setName("coordinador");
		coordinator.setLastName("coordinador");
		coordinator.setInstitutionalId("111226");
		coordinator.setRoles(Set.of(Role.COORDINADOR));

		userRepository.save(professor1);
		userRepository.save(professor2);
		userRepository.save(coord1);
		userRepository.save(coord2);
		userRepository.save(both1);
		userRepository.save(both2);
		userRepository.save(admin);
		userRepository.save(admin2);
		userRepository.save(superAdmin);
		userRepository.save(student);
		userRepository.save(student2);
		userRepository.save(student3);
		userRepository.save(student4);
		userRepository.save(student5);
		userRepository.save(profesor3);
		userRepository.save(coordinator);
	}

	private void insertSimulationsVideosAndComments() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		List<Video> videos = Arrays.asList(
				Video.builder().name("javatechie.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(62L).size(8.3).build(),
				Video.builder().name("10350-224234500_small.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(600L).size(31.2).build(),
				Video.builder().name("CCrit1-1__2025_03_03_18_25_12_Movie.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(600L).size(31.2).build(),
				Video.builder().name("unavailable1.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(210L).size(300.0).available(false).build(),
				Video.builder().name("unavailable2.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(450L).size(420.0).available(false).build(),
				Video.builder().name("unavailable3.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(600L).size(500.0).available(false).build(),
				Video.builder().name("unavailable4.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(780L).size(780.0).available(false).build(),
				Video.builder().name("unavailable5.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(6000L).size(6000.0).available(false).build(),
				Video.builder().name("unavailable6.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(620L).size(500.0).available(false).build(),
				Video.builder().name("unavailable7.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(620L).size(500.0).available(false).build(),
				Video.builder().name("unavailable8.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(620L).size(500.0).available(false).build(),
				Video.builder().name("unavailable9.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(620L).size(500.0).available(false).build());

		videoRepository.saveAll(videos);

		Simulation simulation = simulationRepository.findById(1L).get();
		simulation.setVideo(videoRepository.findById(1L).get());

		simulationRepository.save(simulation);
	}

	private void insertCoursesClassesAndStudents() {
		Course course1 = new Course("Cálculo Diferencial", 123456L, userRepository.findById(1L).get());
		Course course2 = new Course("Cálculo Integral", 123455L, userRepository.findById(16L).get());
		Course course3 = new Course("Cálculo Vectorial", 123454L, userRepository.findById(16L).get());
		Course course4 = new Course("Cálculo 3", 1232234L, userRepository.findById(16L).get());
	
		courseRepository.save(course1);
		courseRepository.save(course2);
		courseRepository.save(course3);
		courseRepository.save(course4);

		ClassModel class1 = new ClassModel("2024-10", userRepository.findAllProfessors(), course2, 12L, 20);
		ClassModel class2 = new ClassModel("2026-10", userRepository.findAllProfessors(), course3, 13L,21);
		ClassModel class3 = new ClassModel("2023-10", userRepository.findAllProfessors(), course3, 14L,25);
		ClassModel class4 = new ClassModel("2024-10", userRepository.findAllProfessors(), course3, 15L,22);
		ClassModel class5 = new ClassModel("2025-10", userRepository.findAllProfessors(), course3, 16L,30);

		class1.setStudents(userRepository.findAllStudents());
		class2.setStudents(userRepository.findAllStudents());
		class3.setStudents(userRepository.findAllStudents());
		class4.setStudents(userRepository.findAllStudents());
		class5.setStudents(userRepository.findAllStudents());

		classRepository.save(class1);
		classRepository.save(class2);
		classRepository.save(class3);
		classRepository.save(class4);
		classRepository.save(class5);
	}

	private void insertPractices() {
		List<Practice> practices = Arrays.asList(
				Practice.builder().name("Practica 1").description(
						"Descripcion de la practica 1. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.GRUPAL).gradeable(true).numberOfGroups(3).maxStudentsGroup(5)
						.classModel(classRepository.findById(1L).get()).simulationDuration(30).build(),
				Practice.builder().name("Practica 2").description(
						"Descripcion de la practica 2. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.INDIVIDUAL).gradeable(true).numberOfGroups(2).maxStudentsGroup(5)
						.classModel(classRepository.findById(1L).get()).simulationDuration(15).build(),
				Practice.builder().name("Practica 3").description(
						"Descripcion de la practica 3. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.INDIVIDUAL).gradeable(true).numberOfGroups(10).maxStudentsGroup(5)
						.classModel(classRepository.findById(1L).get()).simulationDuration(15).build(),
				Practice.builder().name("Practica 4").description(
						"Descripcion de la practica 4. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.GRUPAL).gradeable(true).numberOfGroups(5).maxStudentsGroup(5)
						.classModel(classRepository.findById(1L).get()).simulationDuration(60).build(),
				Practice.builder().name("Practica 5").description(
						"Descripcion de la practica 5. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.GRUPAL).gradeable(true).numberOfGroups(5).maxStudentsGroup(5)
						.classModel(classRepository.findById(1L).get()).simulationDuration(15).build());
		practiceRepository.saveAll(practices);
	}

	private void insertSimulations() {

		LocalDateTime startDateTime = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);

		Simulation simulation1 = Simulation.builder()
			.practice(practiceRepository.findById(1L).get())
			.rooms(Arrays.asList(roomRepository.findById(1L).get(), roomRepository.findById(2L).get()))
			.startDateTime(Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant()))
			.endDateTime(Date.from(startDateTime.plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()))
			.gradeDateTime(Date.from(startDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
			.gradeStatus(GradeStatus.PENDING)
			.grade(5.0f)
			.groupNumber(1)
			.users(userRepository.findAllStudents())
			.build();

		Simulation simulation2 = Simulation.builder()
			.practice(practiceRepository.findById(1L).get())
			.rooms(Arrays.asList(roomRepository.findById(1L).get(), roomRepository.findById(2L).get()))
			.startDateTime(Date.from(startDateTime.plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()))
			.endDateTime(Date.from(startDateTime.plusMinutes(60).atZone(ZoneId.systemDefault()).toInstant()))
			.gradeDateTime(Date.from(startDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
			.gradeStatus(GradeStatus.PENDING)
			.grade(5.0f)
			.groupNumber(2)
			.build();

		Simulation simulation3 = Simulation.builder()
			.practice(practiceRepository.findById(1L).get())
			.rooms(Arrays.asList(roomRepository.findById(1L).get(), roomRepository.findById(2L).get()))
			.startDateTime(Date.from(startDateTime.plusMinutes(60).atZone(ZoneId.systemDefault()).toInstant()))
			.endDateTime(Date.from(startDateTime.plusMinutes(90).atZone(ZoneId.systemDefault()).toInstant()))
			.gradeDateTime(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
			.gradeStatus(GradeStatus.REGISTERED)
			.grade(5.0f)
			.groupNumber(3)
			.build();


			simulationRepository.save(simulation1);
			simulationRepository.save(simulation2);
			simulationRepository.save(simulation3);
	}

	private void insertRubricTemplates() {

	}
}