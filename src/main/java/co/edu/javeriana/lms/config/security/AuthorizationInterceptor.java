package co.edu.javeriana.lms.config.security;

import co.edu.javeriana.lms.accounts.services.AuthService;
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

    public AuthorizationInterceptor(AuthService authService, ClassService classService) {
        this.authService = authService;
        this.classService = classService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AuthorizationInterceptor: Checking authorization for request: {}", request.getRequestURI());
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return false;
        }

        token = token.substring(7); // Eliminar el prefijo "Bearer "
        Long userId = authService.getUserIdByToken(token);
        String[] userRoles = authService.getRolesByToken(token);

        // Obtener la URL y extraer el ID de la clase
        String requestURI = request.getRequestURI();
        Long classId = extractClassIdFromURI(requestURI);

        if (classId != null) {
            // Verificar si el usuario tiene acceso a la clase según su rol
            if (!isUserAuthorizedForClass(userId, userRoles, classId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Forbidden: You do not have access to this class");
                return false;
            }
        }

        return true; // Permitir la solicitud si pasa todas las verificaciones
    }

    private Long extractClassIdFromURI(String uri) {
        // Usar una expresión regular para extraer el ID de la clase
        Pattern pattern = Pattern.compile("/class/(\\d+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null; // No se encontró un ID de clase en la URL
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
}