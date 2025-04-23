package co.edu.javeriana.lms.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Adding AuthorizationInterceptor for patterns");
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/class/**",
                        "/*/clases/**",
                        "/simulation/**",
                        "/grade/class/**",
                        "/grade/student/**");
    }
}