package co.edu.javeriana.lms.shared.errors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {
    // Validation errors (starts with 1)
    VALIDATION_FAILED(1),
    INVALID_DTO(10),
    FORMAT_ERROR(11),
    INVALID_REQUEST_STRUCTURE(12),

    // Authentication errors (starts with 2)
    AUTHENTICATION_FAILED(2),
    INVALID_CREDENTIALS(20),
    TOKEN_NOT_PROVIDED(21),
    INVALID_TOKEN(22),
    EXPIRED_TOKEN(23),

    // Account errors (starts with 3)
    ACCOUNT_ERROR(3),
    ACCOUNT_NOT_FOUND(30),
    ACCOUNT_ALREADY_EXISTS(31),

    // Practice errors (starts with 4)
    PRACTICE_ERROR(4),
    PRACTICE_NOT_FOUND(40),
    // FIXME: Poner error si la sala no tiene capacidad para la práctica
    // FIXME: Poner error si la sala no está disponible en el horario de la práctica
    
    // Simulation errors (starts with 5)
    SIMULATION_ERROR(5),
    SIMULATION_NOT_FOUND(50),
    // FIXME: Poner error al agregar un estudiante y no cabe
    // FIXME: Poner error al agregar un estudiante que ya está en la práctica

    // Room errors (starts with 6)
    ROOM_ERROR(6),
    ROOM_NOT_FOUND(60),
    ROOM_ALREADY_EXISTS(61),
    ROOM_TYPE_NOT_FOUND(62),
    ROOM_TYPE_ALREADY_EXISTS(63),

    // Subject errors (starts with 7)
    SUBJECT_ERROR(7),
    COURSE_ERROR(70),
    COURSE_NOT_FOUND(700),
    COURSE_ALREADY_EXISTS(701),
    CLASS_ERROR(71),
    CLASS_NOT_FOUND(710),
    CLASS_ALREADY_EXISTS(711),

    // Video errors (starts with 8)
    VIDEO_ERROR(8),
    VIDEO_NOT_FOUND(80),
    VIDEO_ALREADY_EXISTS(81),
    ;

    public final Integer code;
}
