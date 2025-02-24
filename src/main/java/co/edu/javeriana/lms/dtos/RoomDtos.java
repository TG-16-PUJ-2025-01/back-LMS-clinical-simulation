package co.edu.javeriana.lms.dtos;

import co.edu.javeriana.lms.models.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDtos {
    @NotBlank(message = "Room name is mandatory")
    private String name;

    @NotNull(message = "Room type is mandatory")
    private RoomTypeDtos type;

    public Room toEntity(Long id){
        Room room = new Room();
        room.setId(id);
        room.setName(this.name);
        room.setType(this.type.toEntity());

        return room;
    }
}
