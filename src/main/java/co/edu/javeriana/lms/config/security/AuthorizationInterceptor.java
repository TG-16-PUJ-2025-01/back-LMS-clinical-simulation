package co.edu.javeriana.lms.config.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import co.edu.javeriana.lms.accounts.services.AuthService;
import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.services.ClassService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        token = token.substring(7); // Eliminar el prefijo "Bearer "
        Long userId = authService.getUserIdByToken(token);
        String[] userRole = authService.getRolesByToken(token);

        // Obtener la URL y extraer el ID del recurso
        String requestURI = request.getRequestURI();

        // Verificar acceso a clases
        if (requestURI.matches("/coordinador/clases/\\d+/practicas")) {
            Long classId = extractIdFromURI(requestURI);

            // Verificar si el usuario tiene acceso a la clase según su rol
            if (!isUserAuthorizedForClass(userId, userRole, classId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        return true; // Permitir la solicitud si pasa todas las verificaciones
    }

    private Long extractIdFromURI(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 2]); // Extraer el ID de la clase
    }

    private boolean isUserAuthorizedForClass(Long userId, String[] userRoles, Long classId) {
        ClassModel classModel = classService.findById(classId);
        if (classModel == null) {
            return false; // La clase no existe
        }

        for (String role : userRoles) {
            if ("ESTUDIANTE".equals(role)) {
                // Verificar si el estudiante está inscrito en la clase
                return classModel.getStudents().stream().anyMatch(student -> student.getId().equals(userId));
            } else if ("PROFESOR".equals(role)) {
                // Verificar si el profesor está asociado a la clase
                return classModel.getProfessors().stream().anyMatch(professor -> professor.getId().equals(userId));
            } else if ("COORDINADOR".equals(role)) {
                // Verificar si el coordinador está asociado a la asignatura de la clase
                return classModel.getCourse().getCoordinator().getId().equals(userId);
            }
        }
        return false; // Rol no autorizado
    }
}