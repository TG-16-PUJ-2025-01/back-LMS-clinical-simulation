package co.edu.javeriana.lms.booking.dtos;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
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

    @NotNull(message = "Room capacity is mandatory")
    private Integer capacity;

    @NotNull(message = "Room IP is mandatory")
    private String ip;

    @NotNull(message = "Room type ID is mandatory")
    private Long typeId;

    public Room toEntity(){
        Room room = new Room();
        room.setId(null);
        room.setName(this.name);
        room.setCapacity(this.capacity);
        room.setIp(this.ip);
        room.setType(RoomType.builder().id(this.typeId).build());

        return room;
    }
}
