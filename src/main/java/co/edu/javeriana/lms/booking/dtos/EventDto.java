package co.edu.javeriana.lms.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDto {
    private int id;
    private String title;
    private String start;
    private String end;
}
