package co.edu.javeriana.lms.dtos;

import co.edu.javeriana.lms.models.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDto {
    private String name;

    public RoomType toEntity(){
        RoomType roomType = new RoomType();
        roomType.setName(this.name);

        return roomType;
    }
}
