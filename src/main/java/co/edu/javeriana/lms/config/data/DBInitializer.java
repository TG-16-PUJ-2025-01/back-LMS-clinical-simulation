package co.edu.javeriana.lms.config.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;
import co.edu.javeriana.lms.grades.models.GradeStatus;
import co.edu.javeriana.lms.practices.dtos.SimulationByTimeSlotDto;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.PracticeType;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.practices.services.SimulationService;
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

	@Autowired 
	private SimulationService simulationService;

	@Override
	public void run(String... args) throws Exception {
		insertRoomsAndTypes();
		createUsers();
		insertCourses();
		insertClasses();
		insertPractices();
		insertSimulations();
		asignStudentsAndGradesToSimulations();
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
				Room.builder().name("Sala1").type(roomTypes.get(0)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala2").type(roomTypes.get(1)).capacity(14).ip("10.43.101.234").build(),
				Room.builder().name("Sala3").type(roomTypes.get(2)).capacity(13).ip("10.43.101.234").build(),
				Room.builder().name("Sala4").type(roomTypes.get(3)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala5").type(roomTypes.get(3)).capacity(1).ip("10.43.101.234").build(),
				Room.builder().name("Sala6").type(roomTypes.get(3)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala7").type(roomTypes.get(4)).capacity(15).ip("10.43.101.234").build(),
				Room.builder().name("Sala8").type(roomTypes.get(4)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala9").type(roomTypes.get(4)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala10").type(roomTypes.get(5)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala11").type(roomTypes.get(5)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala12").type(roomTypes.get(5)).capacity(14).ip("10.43.101.234").build(),
				Room.builder().name("Sala13").type(roomTypes.get(6)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala14").type(roomTypes.get(6)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala15").type(roomTypes.get(6)).capacity(21).ip("10.43.101.234").build(),
				Room.builder().name("Sala16").type(roomTypes.get(7)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala17").type(roomTypes.get(8)).capacity(13).ip("10.43.101.234").build(),
				Room.builder().name("Sala18").type(roomTypes.get(9)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala19").type(roomTypes.get(10)).capacity(51).ip("10.43.101.234").build(),
				Room.builder().name("Sala20").type(roomTypes.get(11)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala21").type(roomTypes.get(11)).capacity(11).ip("10.43.101.234").build(),
				Room.builder().name("Sala22").type(roomTypes.get(11)).capacity(11).ip("10.43.101.234").build());

		roomRepository.saveAll(rooms);
	}

	private void createUsers() {

		List<User> users = Arrays.asList(
				// Profesores
				User.builder().email("profesor@gmail.com").password(passwordEncoder.encode("profesor"))
						.name("María").lastName("López").institutionalId("00000010001")
						.roles(Set.of(Role.PROFESOR)).build(),

				User.builder().email("carlos.gomez@javeriana.edu.co").password(passwordEncoder.encode("123456"))
						.name("Carlos").lastName("Gómez").institutionalId("00000010002")
						.roles(Set.of(Role.PROFESOR)).build(),

				User.builder().email("andres.vera@javeriana.edu.co").password(passwordEncoder.encode("profesor"))
						.name("Andrés").lastName("Vera").institutionalId("00000010003")
						.roles(Set.of(Role.PROFESOR)).build(),

				// Coordinadores
				User.builder().email("coordinador@gmail.com").password(passwordEncoder.encode("coordinador"))
						.name("Laura").lastName("Martínez").institutionalId("00000020001")
						.roles(Set.of(Role.COORDINADOR)).build(),

				User.builder().email("felipe.ramirez@javeriana.edu.co").password(passwordEncoder.encode("123456"))
						.name("Felipe").lastName("Ramírez").institutionalId("00000020002")
						.roles(Set.of(Role.COORDINADOR)).build(),

				User.builder().email("mariana.nieto@javeriana.edu.co").password(passwordEncoder.encode("coordinador"))
						.name("Mariana").lastName("Nieto").institutionalId("00000020003")
						.roles(Set.of(Role.COORDINADOR)).build(),

				// Profesores + Coordinadores
				User.builder().email("juan.perez@javeriana.edu.co").password(passwordEncoder.encode("123456"))
						.name("Juan").lastName("Pérez").institutionalId("00000030001")
						.roles(new HashSet<>(Arrays.asList(Role.PROFESOR, Role.COORDINADOR))).build(),

				User.builder().email("ana.torres@javeriana.edu.co").password(passwordEncoder.encode("123456"))
						.name("Ana").lastName("Torres").institutionalId("00000030002")
						.roles(new HashSet<>(Arrays.asList(Role.PROFESOR, Role.COORDINADOR))).build(),

				// Administradores
				User.builder().email("admin@gmail.com").password(passwordEncoder.encode("admin"))
						.name("Andrés").lastName("García").institutionalId("00000040001")
						.roles(Set.of(Role.ADMIN)).build(),

				User.builder().email("admin@javeriana.edu.co").password(passwordEncoder.encode("admin"))
						.name("Administrador").lastName("General").institutionalId("00000040002")
						.roles(Set.of(Role.ADMIN)).build(),

				// SuperAdmin (todos los roles)
				User.builder().email("superadmin@gmail.com").password(passwordEncoder.encode("superadmin"))
						.name("Sofía").lastName("Admin").institutionalId("00000090001")
						.roles(Set.of(Role.ADMIN, Role.COORDINADOR, Role.PROFESOR, Role.ESTUDIANTE)).build(),

				// Estudiantes
				User.builder().email("estudiante@gmail.com").password(passwordEncoder.encode("estudiante"))
						.name("Camilo").lastName("Mendoza").institutionalId("00000050001")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("jessica.palacios@javeriana.edu.co").password(passwordEncoder.encode("estudiante"))
						.name("Jessica").lastName("Palacios").institutionalId("00000050002")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("daniel.rojas@javeriana.edu.co").password(passwordEncoder.encode("estudiante2"))
						.name("Daniel").lastName("Rojas").institutionalId("00000050003")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("valeria.morales@javeriana.edu.co").password(passwordEncoder.encode("estudiante4"))
						.name("Valeria").lastName("Morales").institutionalId("00000050004")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("julian.fernandez@javeriana.edu.co")
						.password(passwordEncoder.encode("estudiante5"))
						.name("Julián").lastName("Fernández").institutionalId("00000050005")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("sofia.navarro@javeriana.edu.co").password(passwordEncoder.encode("est123"))
						.name("Sofía").lastName("Navarro").institutionalId("00000050006")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("mateo.silva@javeriana.edu.co").password(passwordEncoder.encode("est456"))
						.name("Mateo").lastName("Silva").institutionalId("00000050007")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("isabella.reyes@javeriana.edu.co").password(passwordEncoder.encode("est789"))
						.name("Isabella").lastName("Reyes").institutionalId("00000050008")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("sebastian.ruiz@javeriana.edu.co").password(passwordEncoder.encode("est234"))
						.name("Sebastián").lastName("Ruiz").institutionalId("00000050009")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("valentina.castillo@javeriana.edu.co").password(passwordEncoder.encode("est345"))
						.name("Valentina").lastName("Castillo").institutionalId("00000050010")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("nicolas.mejia@javeriana.edu.co").password(passwordEncoder.encode("est567"))
						.name("Nicolás").lastName("Mejía").institutionalId("00000050011")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("camila.ospina@javeriana.edu.co").password(passwordEncoder.encode("est678"))
						.name("Camila").lastName("Ospina").institutionalId("00000050012")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("daniela.barrera@javeriana.edu.co").password(passwordEncoder.encode("est890"))
						.name("Daniela").lastName("Barrera").institutionalId("00000050013")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("santiago.ortiz@javeriana.edu.co").password(passwordEncoder.encode("est901"))
						.name("Santiago").lastName("Ortiz").institutionalId("00000050014")
						.roles(Set.of(Role.ESTUDIANTE)).build(),

				User.builder().email("juliana.perez@javeriana.edu.co").password(passwordEncoder.encode("est012"))
						.name("Juliana").lastName("Pérez").institutionalId("00000050015")
						.roles(Set.of(Role.ESTUDIANTE)).build()
		);

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

	private void insertClasses() {

		List<User> allProfessors = userRepository.findAllProfessors();
		List<User> allStudents = userRepository.findAllStudents();

		List<ClassModel> classes = List.of(
				ClassModel.builder()
						.period("2025-10")
						.professors(allProfessors.subList(0, 5))
						.course(courseRepository.findById(1L).get())
						.javerianaId(20001L)
						.numberOfParticipants(9)
						.students(allStudents.subList(0,9))
						.build(),
				ClassModel.builder()
						.period("2025-10")
						.professors(allProfessors.subList(0, 4))
						.course(courseRepository.findById(1L).get())
						.javerianaId(20002L)
						.numberOfParticipants(6)
						.students(allStudents.subList(9,15))
						.build(),
				ClassModel.builder()
						.period("2025-10")
						.professors(allProfessors.subList(1, 2))
						.course(courseRepository.findById(2L).get())
						.javerianaId(20003L)
						.numberOfParticipants(15)
						.students(allStudents)
						.build(),
				ClassModel.builder()
						.period("2025-10")
						.professors(allProfessors.subList(3, 5))
						.course(courseRepository.findById(3L).get())
						.javerianaId(20004L)
						.numberOfParticipants(15)
						.students(allStudents)
						.build(),
				ClassModel.builder()
						.period("2025-10")
						.professors(allProfessors)
						.course(courseRepository.findById(4L).get())
						.javerianaId(20005L)
						.numberOfParticipants(15)
						.students(allStudents)
						.build(),
				ClassModel.builder()
						.period("2025-10")
						.professors(allProfessors.subList(4, 5))
						.course(courseRepository.findById(5L).get())
						.javerianaId(20006L)
						.numberOfParticipants(15)
						.students(allStudents)
						.build(),
				ClassModel.builder()
						.period("2025-10")
						.professors(allProfessors.subList(0, 1))
						.course(courseRepository.findById(6L).get())
						.javerianaId(20007L)
						.numberOfParticipants(15)
						.students(allStudents)
						.build(),
				ClassModel.builder()
						.period("2025-30")
						.professors(allProfessors.subList(0, 3))
						.course(courseRepository.findById(7L).get())
						.javerianaId(20008L)
						.numberOfParticipants(15)
						.build());

		classRepository.saveAll(classes);
	}

	private void insertPractices() {
		List<Practice> practices = Arrays.asList(
				Practice.builder().name("Practica 1").description(
						"Descripcion de la practica 1. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.GRUPAL).gradeable(true).numberOfGroups(3).maxStudentsGroup(3)
						.classModel(classRepository.findById(1L).get()).simulationDuration(30).gradePercentage(30f).build(),
				Practice.builder().name("Practica 2").description(
						"Descripcion de la practica 2. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.INDIVIDUAL).gradeable(true).numberOfGroups(9).maxStudentsGroup(1)
						.classModel(classRepository.findById(1L).get()).simulationDuration(15).gradePercentage(40f).build(),
				Practice.builder().name("Practica 3").description(
						"Descripcion de la practica 3. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.GRUPAL).gradeable(true).numberOfGroups(3).maxStudentsGroup(3)
						.classModel(classRepository.findById(1L).get()).simulationDuration(15).gradePercentage(30f).build(),
				Practice.builder().name("Practica 4").description(
						"Descripcion de la practica 4. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi viverra dictum fermentum.")
						.type(PracticeType.GRUPAL).gradeable(false).numberOfGroups(5).maxStudentsGroup(5)
						.classModel(classRepository.findById(1L).get()).simulationDuration(15).build());
		practiceRepository.saveAll(practices);
	}

	private void insertSimulations() {	
		LocalDateTime baseDate = LocalDateTime.now()
				.withHour(9).withMinute(0).withSecond(0).withNano(0)
				.with(java.time.DayOfWeek.MONDAY)
				.minusWeeks(2L);

		List<SimulationByTimeSlotDto> simulationsPractice1 = Arrays.asList(
			
				SimulationByTimeSlotDto.builder().practiceId(1L)
						.roomIds(Arrays.asList(1L, 2L))
						.startDateTime(Date.from(baseDate.atZone(ZoneId.systemDefault()).toInstant()))
						.endDateTime(Date.from(baseDate.plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()))
						.build(),

				SimulationByTimeSlotDto.builder().practiceId(1L)
						.roomIds(Arrays.asList(1L, 2L))
						.startDateTime(Date.from(baseDate.plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
						.endDateTime(Date.from(baseDate.plusDays(1).plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()))
						.build(),

				SimulationByTimeSlotDto.builder().practiceId(1L)
						.roomIds(Arrays.asList(1L, 2L))
						.startDateTime(Date.from(baseDate.plusDays(2).atZone(ZoneId.systemDefault()).toInstant()))
						.endDateTime(Date.from(baseDate.plusDays(2).plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()))
						.build()
		);

		simulationService.addSimulations(simulationsPractice1);

		List<SimulationByTimeSlotDto> simulationsPractice2 = Arrays.asList(
				SimulationByTimeSlotDto.builder().practiceId(2L)
						.roomIds(Arrays.asList(1L, 2L))
						.startDateTime(Date.from(baseDate.plusWeeks(1L).atZone(ZoneId.systemDefault()).toInstant()))
						.endDateTime(Date.from(baseDate.plusWeeks(1L).plusMinutes(135).atZone(ZoneId.systemDefault()).toInstant()))
						.build()
		);
		
		simulationService.addSimulations(simulationsPractice2);

		List<SimulationByTimeSlotDto> simulationsPractice3 = Arrays.asList(
				SimulationByTimeSlotDto.builder().practiceId(3L)
						.roomIds(Arrays.asList(1L, 2L))
						.startDateTime(Date.from(baseDate.plusWeeks(2L).atZone(ZoneId.systemDefault()).toInstant()))
						.endDateTime(Date.from(baseDate.plusWeeks(2L).plusMinutes(45).atZone(ZoneId.systemDefault()).toInstant()))
						.build()
		);

		simulationService.addSimulations(simulationsPractice3);
	}

	@Transactional
	private void asignStudentsAndGradesToSimulations() {
		ClassModel classModel = classRepository.findById(1L).get();
		List<User> students = classRepository.findStudentsMembers(1L);
		List<Practice> practices =  practiceRepository.findByClassModel_ClassId(classModel.getClassId());

		for (Practice practice: practices){
			List<User> tempUsers = new ArrayList<>(students);
			int numberOfGroups = practice.getNumberOfGroups();
			int maxStudentsGroup = practice.getMaxStudentsGroup();
			
			// Create groups of students and assign them to simulations
			List<List<User>> groups = new ArrayList<>();
			for (int i = 0; i < numberOfGroups; i++) {
				List<User> group = new ArrayList<>();
				for (int j = 0; j < maxStudentsGroup; j++) {
					if (!tempUsers.isEmpty()) {
						group.add(tempUsers.remove(0));
					}
				}
				groups.add(group);
			}

			for (Simulation simulation: simulationRepository.findByPracticeId(practice.getId())){
				simulation.setUsers(groups.remove(0));
				Random random = new Random();
				float grade = 3.0f + random.nextFloat() * 2.0f; 
				simulation.setGrade((float) (Math.round(grade * 10.0) / 10.0)); 
				simulation.setGradeStatus(GradeStatus.REGISTERED);
				simulation.setGradeDateTime(new Date());
				simulationRepository.save(simulation);
			}
		}
	}

	private void insertSimulationsVideosAndComments() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		List<Video> videos = Arrays.asList(
				Video.builder().name("javatechie.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(62L).size(8.3).build(),
				Video.builder().name("10350-224234500_small.mp4").recordingDate(dateFormat.parse("2023-01-31"))
						.expirationDate(new Date()).duration(600L).size(31.2).build(),
				Video.builder().name("CCrit1-1__2025_03_03_18_25_12_Movie.mp4")
						.recordingDate(dateFormat.parse("2023-01-31"))
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
}