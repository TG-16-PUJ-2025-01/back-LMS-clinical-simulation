package co.edu.javeriana.lms.dtos;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.lms.models.Room;

public class RoomMapper {
    
    public static RoomDto toDto(Room room) {
        return new RoomDto(room.getId(), room.getName(), RoomTypeMapper.toDto(room.getType()));
    }
    
    public static Room toEntity(RoomDto room) {
        return new Room(room.getId(), room.getName(), RoomTypeMapper.toEntity(room.getType()));
    }
    
    public static List<RoomDto> toDtos(List<Room> rooms) {
        return rooms.stream().map(RoomMapper::toDto).collect(Collectors.toList());
    }
    
    public static List<Room> toEntities(List<RoomDto> rooms) {
        return rooms.stream().map(RoomMapper::toEntity).collect(Collectors.toList());
    }   
    
}
