package co.edu.javeriana.lms.dtos;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.javeriana.lms.models.RoomType;

public class RoomTypeMapper {
    
    public static RoomTypeDto toDto(RoomType type) {
        return new RoomTypeDto(type.getId(), type.getName());
    }
    
    public static RoomType toEntity(RoomTypeDto type) {
        return new RoomType(type.getId(), type.getName());
    }
    
    public static List<RoomTypeDto> toDtos(List<RoomType> types) {
        return types.stream().map(RoomTypeMapper::toDto).collect(Collectors.toList());
    }
    
    public static List<RoomType> toEntities(List<RoomTypeDto> types) {
        return types.stream().map(RoomTypeMapper::toEntity).collect(Collectors.toList());
    }
    
}
