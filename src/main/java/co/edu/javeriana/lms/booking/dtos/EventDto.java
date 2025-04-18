package co.edu.javeriana.lms.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String start;
    private String end;
    private String calendarId;
}