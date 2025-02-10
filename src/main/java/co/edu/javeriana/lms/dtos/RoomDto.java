package co.edu.javeriana.lms.dtos;

public class RoomDto {
    private Long id;
    private String name;
    private RoomTypeDto type;
    
    public RoomDto() {
        super();
    }

    public RoomDto(Long id, String name, RoomTypeDto type) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoomTypeDto getType() {
        return type;
    }

    public void setType(RoomTypeDto type) {
        this.type = type;
    }
    
}
