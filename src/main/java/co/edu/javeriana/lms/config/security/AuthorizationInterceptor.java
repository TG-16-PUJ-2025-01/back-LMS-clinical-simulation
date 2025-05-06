package co.edu.javeriana.lms.config.security;

import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.practices.models.Practice;
import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.services.PracticeService;
import co.edu.javeriana.lms.practices.services.SimulationService;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.services.ClassService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final ClassService classService;
    private final SimulationService simulationService;
    private final PracticeService practiceService;

    public AuthorizationInterceptor(AuthService authService, ClassService classService,
            SimulationService simulationService, PracticeService practiceService) {
        this.authService = authService;
        this.classService = classService;
        this.simulationService = simulationService;
        this.practiceService = practiceService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.info("AuthorizationInterceptor: Checking authorization for request: {}", request.getRequestURI());
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return false;
        }

        token = token.substring(7);
        Long userId = authService.getUserIdByToken(token);
        String[] userRoles = authService.getRolesByToken(token);

        String requestURI = request.getRequestURI();

        // Checks for routes related to practices
        if (requestURI.contains("/practice/")) {
            Long practiceId = extractPracticeIdFromURI(requestURI);
            if (practiceId != null) {
                if (!isUserAuthorizedForPractice(userId, userRoles, practiceId)) {
                    log.error("User {} is not authorized for practice {}", userId, practiceId);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: You do not have access to this practice");
                    return false;
                }
            }
        }

        // Check for routes related to grades
        if (requestURI.contains("/grade/")) {
            Long classId = extractClassIdFromURIForGrades(requestURI);
            if (classId != null) {
                if (!isUserAuthorizedForClass(userId, userRoles, classId)) {
                    log.error("User {} is not authorized for class {} grades ", userId, classId);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: You do not have access to this class");
                    return false;
                }
            }
        }

        // Checks for routes related to class members
        if (requestURI.contains("/member/")) {
            Long classId = extractClassIdFromURIForMembers(requestURI);
            if (classId != null) {
                if (!isUserAuthorizedForClass(userId, userRoles, classId)) {
                    log.error("User {} is not authorized to access members of class {}", userId, classId);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: You do not have access to this class members");
                    return false;
                }
            }
        }

        // Checks for routes related to classes
        if (requestURI.contains("/class/")) {
            Long classId = extractClassIdFromURI(requestURI);
            if (classId != null) {
                if (!isUserAuthorizedForClass(userId, userRoles, classId)) {
                    log.error("User {} is not authorized for class {}", userId, classId);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: You do not have access to this class");
                    return false;
                }
            }
        }

        // Checks for routes related to simulations
        if (requestURI.contains("/simulation/")) {
            Long simulationId = extractSimulationIdFromURI(requestURI);
            if (simulationId != null) {
                Simulation simulation = simulationService.findSimulationById(simulationId);
                Long classId = simulation.getPractice().getClassModel().getClassId();

                if (!isUserAuthorizedForSimulation(userId, userRoles, simulationId, classId)) {
                    log.error("User {} is not authorized for simulation {}", userId, simulationId);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: You do not have access to this simulation");
                    return false;
                }
            }
        }

        return true; // Allow the request if it passes all checks
    }

    private Long extractClassIdFromURI(String uri) {
        Pattern pattern = Pattern.compile("/class/(\\d+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private Long extractClassIdFromURIForMembers(String uri) {
        Pattern pattern = Pattern.compile("/class/(\\d+)/member");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private Long extractClassIdFromURIForGrades(String uri) {
        Pattern pattern = Pattern.compile("/grade/class/(\\d+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private Long extractPracticeIdFromURI(String uri) {
        Pattern pattern = Pattern.compile("/practice/(\\d+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private Long extractSimulationIdFromURI(String uri) {
        Pattern pattern = Pattern.compile("/simulation/(\\d+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private boolean isUserAuthorizedForClass(Long userId, String[] userRoles, Long classId) {
        ClassModel classModel = classService.findById(classId);
        if (classModel == null) {
            return false; // Class does not exist, return false
        }

        for (String role : userRoles) {
            switch (role) {
                case "ESTUDIANTE":
                    // Check if the student is enrolled in the class
                    if (classModel.getStudents().stream().anyMatch(student -> student.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "PROFESOR":
                    // Check if the professor is associated with the class
                    if (classModel.getProfessors().stream().anyMatch(professor -> professor.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "COORDINADOR":
                    // Check if the coordinator is associated with the course of the class
                    if (classModel.getCourse().getCoordinator().getId().equals(userId)) {
                        return true;
                    }
                    break;
                case "ADMIN":
                    return true; // Admin does not have restrictions
                default:
                    break;
            }
        }

        return false; // No role has access to the class
    }

    private boolean isUserAuthorizedForSimulation(Long userId, String[] userRoles, Long simulationId, Long classId) {
        Simulation simulation = simulationService.findSimulationById(simulationId);
        if (simulation == null) {
            return false; // Simulation does not exist, return false
        }

        for (String role : userRoles) {
            switch (role) {
                case "ESTUDIANTE":
                    // Check if the student is enrolled in the simulation
                    if (simulation.getUsers().stream().anyMatch(user -> user.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "PROFESOR":
                    // Check if the professor is associated with the class of the simulation
                    ClassModel classModel = classService.findById(classId);
                    if (classModel != null && classModel.getProfessors().stream()
                            .anyMatch(professor -> professor.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "COORDINADOR":
                    ClassModel coordinatorClass = classService.findById(classId);
                    if (coordinatorClass != null
                            && coordinatorClass.getCourse().getCoordinator().getId().equals(userId)) {
                        return true;
                    }
                    break;
                case "ADMIN":
                    return true; // Admin does not have restrictions
                default:
                    break;
            }
        }

        return false; // No role has access
    }

    private boolean isUserAuthorizedForPractice(Long userId, String[] userRoles, Long practiceId) {
        Practice practice = practiceService.findById(practiceId);
        if (practice == null) {
            return false; // Practice does not exist, return false
        }

        // Obtain the ID of the class associated with the practice
        Long classId = practice.getClassModel().getClassId();

        // Reutilize the logic of isUserAuthorizedForClass to validate access to the
        // class
        return isUserAuthorizedForClass(userId, userRoles, classId);
    }
}
