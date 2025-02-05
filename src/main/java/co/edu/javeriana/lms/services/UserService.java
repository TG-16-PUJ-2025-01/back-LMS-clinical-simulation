package co.edu.javeriana.lms.services;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
