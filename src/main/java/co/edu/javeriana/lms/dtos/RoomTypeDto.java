package co.edu.javeriana.lms.dtos;

import co.edu.javeriana.lms.booking.models.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeDto {
    private String name;

    public RoomType toEntity(){
        RoomType roomType = new RoomType();
        roomType.setName(this.name);

        return roomType;
    }
}
