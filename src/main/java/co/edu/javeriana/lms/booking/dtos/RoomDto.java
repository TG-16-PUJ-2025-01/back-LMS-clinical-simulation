package co.edu.javeriana.lms.booking.dtos;

import co.edu.javeriana.lms.booking.models.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    @NotBlank(message = "Room name is mandatory")
    private String name;

    @NotNull(message = "Room type is mandatory")
    private RoomTypeDto type;

    public Room toEntity(Long id){
        Room room = new Room();
        room.setId(id);
        room.setName(this.name);
        room.setType(this.type.toEntity());

        return room;
    }
}
