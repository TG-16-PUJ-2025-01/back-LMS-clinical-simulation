package co.edu.javeriana.lms.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.User;

@Service
public class UserService {

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                // TODO Search user in database
                return new User();
            }
        };
    }
}