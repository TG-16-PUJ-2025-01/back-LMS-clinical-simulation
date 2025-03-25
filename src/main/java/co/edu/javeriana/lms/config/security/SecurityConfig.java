package co.edu.javeriana.lms.config.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import co.edu.javeriana.lms.accounts.models.Role;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private Environment env;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.frameOptions(t -> t.disable()));

        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                        .access((authentication, context) -> new AuthorizationDecision(env.matchesProfiles("dev")))
                        .requestMatchers("/streaming/**").permitAll() // Public endpoint
                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name()) // Admin-only
                        .requestMatchers("/profesor/**").hasAuthority(Role.PROFESOR.name()) // Profesor-only
                        .requestMatchers("/estudiante/**").hasAuthority(Role.ESTUDIANTE.name()) // Estudiante-only
                        .requestMatchers("/coordinador/**").hasAuthority(Role.COORDINADOR.name()) // Coordinador-only

                        .requestMatchers("/course/delete/**").hasAuthority(Role.ADMIN.name()) // Coordinador-only
                        .requestMatchers("/course/add/**").hasAuthority(Role.ADMIN.name()) // Coordinador-only
                        .requestMatchers("/course/update/**").hasAuthority(Role.ADMIN.name()) // Coordinador-only
                        .requestMatchers("/course/all").hasAnyAuthority(Role.ADMIN.name(), Role.COORDINADOR.name()) // Coordinador-only
                        .requestMatchers("/course/{id}").hasAnyAuthority(Role.ADMIN.name(), Role.COORDINADOR.name()) // Coordinador-only

                        .requestMatchers("/class/all").hasAnyAuthority(Role.ADMIN.name())
                        .requestMatchers("/class/{id}").authenticated()
                        .requestMatchers("/class/add")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.PROFESOR.name(), Role.COORDINADOR.name())
                        .requestMatchers("/class/update/{id}")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.PROFESOR.name(), Role.COORDINADOR.name())
                        .requestMatchers("/class/delete/{id}").hasAnyAuthority(Role.ADMIN.name(), Role.COORDINADOR.name())
                        .requestMatchers("/class/{id}/member/all").authenticated()
                        .requestMatchers("/class/{id}/member/all/outside")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.PROFESOR.name(), Role.COORDINADOR.name())
                        .requestMatchers("/class/delete/{id}/member/{idMember}")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.COORDINADOR.name(), Role.PROFESOR.name())
                        .requestMatchers("/class/update/{id}/members")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.PROFESOR.name(), Role.COORDINADOR.name())
                        .requestMatchers("/class/add")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.PROFESOR.name(), Role.COORDINADOR.name())

                        .requestMatchers("/rubric/**").hasAnyAuthority(Role.COORDINADOR.name(), Role.PROFESOR.name(),Role.ADMIN.name()) // Coordinador-only

                        .requestMatchers("/auth/login").permitAll() // Public endpoint
                        .requestMatchers("/auth/change-password").authenticated() // Authenticated endpoint
                        .requestMatchers("/reset-password/**").permitAll() // Public endpoint
                        .requestMatchers("/user/**").hasAuthority(Role.ADMIN.name())// Authenticated endpoint
                        .requestMatchers("/room/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/practice/**")
                        .hasAnyAuthority(Role.ADMIN.name(), Role.COORDINADOR.name(), Role.PROFESOR.name())
                        .anyRequest().permitAll())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized");
                            log.error("Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Forbidden");
                            log.error("Forbidden");
                        }))
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("PUT", "DELETE", "GET", "POST", "PATCH");
            }
        };
    }
}