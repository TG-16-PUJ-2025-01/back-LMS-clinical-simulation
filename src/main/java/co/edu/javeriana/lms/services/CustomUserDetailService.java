package co.edu.javeriana.lms.services;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.accounts.models.Role;
import co.edu.javeriana.lms.accounts.models.User;
import co.edu.javeriana.lms.repositories.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Map roles to authorities
        Collection<GrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        // Return a Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        // Map all roles to authorities
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name())) // Use role.name() if Role is an enum
                .collect(Collectors.toList());
    }

    public Collection<String> getUserRolesFromToken(String token) {
        if (jwtService.isTokenValid(token, loadUserByUsername(jwtService.extractUserName(token)))) {
            // Extract the username (email) from the token
            String email = jwtService.extractUserName(token);

            // Load user details from the database
            UserDetails userDetails = loadUserByUsername(email);

            // Return all user roles
            return userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Invalid JWT token");
    }
}
