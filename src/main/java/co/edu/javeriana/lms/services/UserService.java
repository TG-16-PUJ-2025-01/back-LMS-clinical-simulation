package co.edu.javeriana.lms.services;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return new UserDetails() {
                    @Override
                    public String getPassword() {
                        return null;
                    }

                    @Override
                    public String getUsername() {
                        return null;
                    }

                    @Override
                    public boolean isAccountNonExpired() {
                        return false;
                    }

                    @Override
                    public boolean isAccountNonLocked() {
                        return false;
                    }

                    @Override
                    public boolean isCredentialsNonExpired() {
                        return false;
                    }

                    @Override
                    public boolean isEnabled() {
                        return false;
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }
                };
            }
        };
    }
}