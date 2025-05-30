package co.edu.javeriana.lms.accounts.models;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.javeriana.lms.grades.models.RubricTemplate;
import co.edu.javeriana.lms.practices.models.Simulation;

import co.edu.javeriana.lms.subjects.models.ClassModel;
import co.edu.javeriana.lms.subjects.models.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "institutionalId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Column
    private String name;

    @Column
    private String lastName;

    @Column
    private String institutionalId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    private Role preferredRole;

    @ManyToMany
    @JoinTable(name = "professor_classes", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "classId"))
    @JsonIgnore
    private List<ClassModel> professorClasses;

    @OneToMany(mappedBy = "coordinator")
    @JsonIgnore
    private List<Course> courses;

    @ManyToMany
    @JoinTable(name = "class_students", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "classId"))
    @JsonIgnore
    private List<ClassModel> studentClasses;

    @ManyToMany
    @JoinTable(name = "simulation_users", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "simulationId"))
    @JsonIgnore
    private List<Simulation> simulations;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private List<RubricTemplate> rubricTemplates;
}
