package co.edu.javeriana.lms.config.security;

import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.practices.models.Simulation;
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

    public AuthorizationInterceptor(AuthService authService, ClassService classService, SimulationService simulationService) {
        this.authService = authService;
        this.classService = classService;
        this.simulationService = simulationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.error("AuthorizationInterceptor: Checking authorization for request: {}", request.getRequestURI());
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return false;
        }

        token = token.substring(7); // Eliminar el prefijo "Bearer"
        Long userId = authService.getUserIdByToken(token);
        String[] userRoles = authService.getRolesByToken(token);

        String requestURI = request.getRequestURI();

        // Validación para rutas relacionadas con clases
        if (requestURI.contains("/class/") || requestURI.contains("/grade/")) {
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

        // Validación para rutas relacionadas con simulaciones
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

        return true; // Permitir la solicitud si pasa todas las verificaciones
    }

    private Long extractClassIdFromURI(String uri) {
        // Usar una expresión regular para extraer el ID de la clase
        Pattern pattern = Pattern.compile("/class/(\\d+)"); //TODO: No esta atrapando bien el id de la clase de calificaciones
        // TODO: Creo que es por el de percentages, tambien atrapa esa y creo no es necesaria
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null; // No se encontró un ID de clase en la URL
    }

    private Long extractSimulationIdFromURI(String uri) {
        // Usar una expresión regular para extraer el ID de la simulación
        Pattern pattern = Pattern.compile("/simulation/(\\d+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null; // No se encontró un ID de simulación en la URL
    }

    private boolean isUserAuthorizedForClass(Long userId, String[] userRoles, Long classId) {
        ClassModel classModel = classService.findById(classId);
        if (classModel == null) {
            return false; // La clase no existe
        }

        for (String role : userRoles) {
            switch (role) {
                case "ESTUDIANTE":
                    // Verificar si el estudiante está inscrito en la clase
                    if (classModel.getStudents().stream().anyMatch(student -> student.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "PROFESOR":
                    // Verificar si el profesor está asociado a la clase
                    if (classModel.getProfessors().stream().anyMatch(professor -> professor.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "COORDINADOR":
                    // Verificar si el coordinador está asociado a la asignatura de la clase
                    if (classModel.getCourse().getCoordinator().getId().equals(userId)) {
                        return true;
                    }
                    break;
                default:
                    // Rol no reconocido, continuar con la verificación
                    break;
            }
        }

        return false; // Ningún rol tiene acceso
    }

    private boolean isUserAuthorizedForSimulation(Long userId, String[] userRoles, Long simulationId, Long classId) {
        Simulation simulation = simulationService.findSimulationById(simulationId);
        if (simulation == null) {
            return false; // La simulación no existe
        }
    
        for (String role : userRoles) {
            switch (role) {
                case "ESTUDIANTE":
                    if (simulation.getUsers().stream().anyMatch(user -> user.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "PROFESOR":
                    ClassModel classModel = classService.findById(classId);
                    if (classModel != null && classModel.getProfessors().stream().anyMatch(professor -> professor.getId().equals(userId))) {
                        return true;
                    }
                    break;
                case "COORDINADOR":
                    ClassModel coordinatorClass = classService.findById(classId);
                    if (coordinatorClass != null && coordinatorClass.getCourse().getCoordinator().getId().equals(userId)) {
                        return true;
                    }
                    break;
                case "ADMIN":
                    return true; // El administrador tiene acceso a todo
                default:
                    break;
            }
        }
    
        return false; // Ningún rol tiene acceso
    }
}