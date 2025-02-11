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
public class RoomDto {
    @NotBlank(message = "El nombre de la sala no puede estar vacío")
    private String name;

    @NotNull(message = "El tipo de la sala es obligatorio")
    private RoomTypeDto type;

    public Room toEntity(Long id){
        Room room = new Room();
        room.setId(id);
        room.setName(this.name);
        room.setType(this.type.toEntity());

        return room;
    }
}
