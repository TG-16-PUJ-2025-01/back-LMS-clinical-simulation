package co.edu.javeriana.lms.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Role {
    ADMIN, 
    PROFESOR, 
    ESTUDIANTE, 
    COORDINADOR;
}
