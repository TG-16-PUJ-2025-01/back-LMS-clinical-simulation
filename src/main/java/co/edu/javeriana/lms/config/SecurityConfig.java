package co.edu.javeriana.lms.config;

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
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // Admin-only
                        .requestMatchers("/profesor/**").hasAuthority("profesor") // Profesor-only
                        .requestMatchers("/estudiante/**").hasAuthority("estudiante") // Estudiante-only
                        .requestMatchers("/coordinador/**").hasAuthority("coordinador") // Coordinador-only

                        .requestMatchers("/course/delete/**").hasAuthority("ADMIN") // Coordinador-only
                        .requestMatchers("/course/add/**").hasAuthority("ADMIN") // Coordinador-only
                        .requestMatchers("/course/update/**").hasAuthority("ADMIN") // Coordinador-only
                        .requestMatchers("/course/all").hasAuthority("ADMIN") // Coordinador-only
                        .requestMatchers("/course/{id}").hasAnyAuthority("ADMIN", "coordinador") // Coordinador-only

                        .requestMatchers("/class/all").hasAnyAuthority("ADMIN")
                        .requestMatchers("/class/{id}").authenticated()
                        .requestMatchers("/class/add").hasAnyAuthority("ADMIN", "profesor", "coordinador")
                        .requestMatchers("/class/update/{id}").hasAnyAuthority("ADMIN", "profesor", "coordinador")
                        .requestMatchers("/class/delete/{id}").hasAnyAuthority("ADMIN", "coordinador")
                        .requestMatchers("/class/{id}/member/all").authenticated()
                        .requestMatchers("/class/{id}/member/all/outside")
                        .hasAnyAuthority("ADMIN", "profesor", "coordinador")
                        .requestMatchers("/class/delete/{id}/member/{idMember}")
                        .hasAnyAuthority("ADMIN", "coordinador", "profesor")
                        .requestMatchers("/class/update/{id}/members")
                        .hasAnyAuthority("ADMIN", "profesor", "coordinador")
                        .requestMatchers("/class/add").hasAnyAuthority("ADMIN", "profesor", "coordinador")

                        .requestMatchers("/auth/login").permitAll() // Public endpoint
                        .requestMatchers("/auth/change-password").authenticated() // Authenticated endpoint
                        .requestMatchers("/reset-password/**").permitAll() // Public endpoint
                        .requestMatchers("/user/**").permitAll() // Public endpoint
                        .anyRequest().permitAll()) // All other requests require authentication
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