package co.edu.javeriana.lms.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.accounts.repositories.UserRepository;
import co.edu.javeriana.lms.grades.dtos.PracticePercentageDto;
import co.edu.javeriana.lms.grades.dtos.PracticesPercentagesDto;
import co.edu.javeriana.lms.grades.dtos.StudentGradeDto;
import co.edu.javeriana.lms.grades.services.GradeService;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.PracticeType;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.PracticeRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.repositories.ClassRepository;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
public class GradeServiceTest {

    @InjectMocks
    private GradeService gradeService;

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private PracticeRepository practiceRepository;

    @Mock
    private UserRepository userRepository;

    private static Long mockClassId = 1L;
    private static ClassModel mockClass;
    private static Simulation mockSimulation1;
    private static Simulation mockSimulation2;
    private static Long mockPracticeId1 = 1L;
    private static Practice mockPractice1;
    private static Long mockPracticeId2 = 2L;
    private static Practice mockPractice2;
    private static Long mockUserId = 1L;
    private static User mockUser;

    @BeforeAll
    public static void setUpAll() {
        mockClass = ClassModel.builder()
                .javerianaId(123456L)
                .numberOfParticipants(20)
                .period("2023-1")
                .build();
        mockPractice1 = Practice.builder()
                .id(mockPracticeId1)
                .classModel(mockClass)
                .name("Practice 1")
                .description("Practice 1")
                .maxStudentsGroup(5)
                .numberOfGroups(2)
                .gradeable(true)
                .gradePercentage(0.5f)
                .simulationDuration(500)
                .type(PracticeType.GRUPAL)
                .build();
        mockPractice2 = Practice.builder()
                .id(mockPracticeId2)
                .classModel(mockClass)
                .name("Practice 2")
                .description("Practice 2")
                .maxStudentsGroup(5)
                .numberOfGroups(3)
                .gradeable(true)
                .gradePercentage(0.5f)
                .simulationDuration(750)
                .type(PracticeType.GRUPAL)
                .build();
        mockUser = User.builder()
                .id(mockUserId)
                .email("mock@gmail.com")
                .password("mockPassword")
                .roles(Set.of(Role.ADMIN, Role.COORDINADOR))
                .preferredRole(Role.ADMIN)
                .name("Mock User")
                .lastName("Mock Last Name")
                .institutionalId("123456")
                .build();
        mockSimulation1 = Simulation.builder()
                .grade(4.5f)
                .gradeDateTime(new Date())
                .groupNumber(1)
                .users(List.of(mockUser))
                .build();
        mockSimulation2 = Simulation.builder()
                .grade(3.5f)
                .gradeDateTime(new Date())
                .groupNumber(2)
                .users(List.of(mockUser))
                .build();
        mockClass.setPractices(List.of(mockPractice1, mockPractice2));
        mockSimulation1.setPractice(mockPractice1);
        mockSimulation2.setPractice(mockPractice2);
    }

    @Test
    public void testGetFinalGradesByClass() {
        when(classRepository.findById(mockClassId)).thenReturn(Optional.of(mockClass));
        when(simulationRepository.findAllByPractice_ClassModel(mockClass))
                .thenReturn(List.of(mockSimulation1, mockSimulation2));

        List<StudentGradeDto> grades = gradeService.getFinalGradesByClass(mockClassId);

        assert grades.size() == 2;
        assert grades.get(0).getFinalGrade() == 4.5f;
        assert grades.get(1).getFinalGrade() == 3.5f;
    }

    @Test
    public void testGetFinalGradesByClassWithClassNotFound() {
        when(classRepository.findById(mockClassId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            gradeService.getFinalGradesByClass(mockClassId);
        });

    }

    @Test
    public void testUpdateClassGradePercentages() {
        when(practiceRepository.findById(mockPracticeId1)).thenReturn(Optional.of(mockPractice1));
        when(practiceRepository.findById(mockPracticeId2)).thenReturn(Optional.of(mockPractice2));

        gradeService.updateClassGradePercentages(PracticesPercentagesDto.builder()
                .practicesPercentages(List.of(
                        PracticePercentageDto.builder().practiceId(mockPracticeId1).percentage(0.6f).build(),
                        PracticePercentageDto.builder().practiceId(mockPracticeId2).percentage(0.4f).build()))
                .build());

        Practice updatedPractice1 = Practice.builder()
                .id(mockPracticeId1)
                .classModel(mockClass)
                .name(mockPractice1.getName())
                .description(mockPractice1.getDescription())
                .maxStudentsGroup(mockPractice1.getMaxStudentsGroup())
                .numberOfGroups(mockPractice1.getNumberOfGroups())
                .gradeable(mockPractice1.getGradeable())
                .gradePercentage(0.6f)
                .simulationDuration(mockPractice1.getSimulationDuration())
                .type(mockPractice1.getType())
                .build();

        Practice updatedPractice2 = Practice.builder()
                .id(mockPracticeId2)
                .classModel(mockClass)
                .name(mockPractice2.getName())
                .description(mockPractice2.getDescription())
                .maxStudentsGroup(mockPractice2.getMaxStudentsGroup())
                .numberOfGroups(mockPractice2.getNumberOfGroups())
                .gradeable(mockPractice2.getGradeable())
                .gradePercentage(0.4f)
                .simulationDuration(mockPractice2.getSimulationDuration())
                .type(mockPractice2.getType())
                .build();

        verify(practiceRepository).save(updatedPractice1);
        verify(practiceRepository).save(updatedPractice2);
    }

    @Test
    public void testGetGradesByUserAndClass() {
        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
        when(classRepository.findById(mockClassId)).thenReturn(Optional.of(mockClass));
        when(simulationRepository.findAllByPractice_ClassModel(mockClass))
                .thenReturn(List.of(mockSimulation1, mockSimulation2));

        StudentGradeDto grades = gradeService.getGradesByUserAndClass(mockUserId, mockClassId);

        assertEquals(mockUser.getName(), grades.getStudentName());
        assert grades.getPracticeGrades().size() == 2;
        assert grades.getPracticeGrades().get(mockPractice1.getName()) == mockSimulation1.getGrade();
    }
}
