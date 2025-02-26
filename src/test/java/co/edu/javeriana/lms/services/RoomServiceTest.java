package co.edu.javeriana.lms.services;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;

@SpringBootTest
public class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    private static Room mockRoom;
    private static RoomType mockRoomType;
    private static Page<Room> mockRoomsPage;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setUpAll() {
        mockRoomType = RoomType.builder().id(1L).name("Cirugia").build();

        Room room1 = Room.builder().id(1L).name("Room A").type(mockRoomType).build();
        Room room2 = Room.builder().id(2L).name("Room B").type(mockRoomType).build();

        mockRoom = room1;

        List<Room> mockRooms = Arrays.asList(room1, room2);

        mockRoomsPage = new PageImpl<>(mockRooms, PageRequest.of(0, 10, Sort.by("name").ascending()), mockRooms.size());
    }

    @Test
    public void testSearchRooms() {
        when(roomRepository.findByNameContaining("", mockRoomsPage.getPageable())).thenReturn(mockRoomsPage);

        Page<Room> roomsPage = roomService.searchRooms("", 0, 10, "name", true);

        assert (roomsPage.getTotalElements() == mockRoomsPage.getTotalElements());
        assert (roomsPage.getContent().size() == mockRoomsPage.getContent().size());
        assert (roomsPage.getContent().equals(mockRoomsPage.getContent()));
        assert (roomsPage.getNumber() == mockRoomsPage.getNumber());
        assert (roomsPage.getSize() == mockRoomsPage.getSize());
    }

    @Test
    public void testEditRoomSuccess() {
        Long id = 1L;
        when(roomRepository.findById(id)).thenReturn(Optional.of(mockRoom));
        when(roomRepository.save(mockRoom)).thenReturn(mockRoom);

        Room editedRoom = roomService.update(mockRoom);

        assert (editedRoom != null);
        assert (editedRoom.getName().equals(mockRoom.getName()));
    }

    @Test
    public void testEditRoomFailure() {
        Long id = 1L;
        Room roomWithNullType = Room.builder().id(id).name("Updated Room")
                .type(RoomType.builder().name("Cirugia").build()).build();
        when(roomRepository.findById(id)).thenReturn(Optional.empty());

        Room editedRoom = roomService.update(roomWithNullType);

        assert (editedRoom == null);
    }

    @Test
    public void testDeleteRoomSuccess() {
        Long id = 1L;
        when(roomRepository.findById(id)).thenReturn(Optional.of(mockRoom));

        roomService.deleteById(id);

        verify(roomRepository, times(1)).deleteById(id);
        assert (roomRepository.findById(id).isPresent());
    }

    @Test
    public void testDeleteRoomFailure() {
        Long id = 1L;
        when(roomRepository.findById(id)).thenReturn(Optional.empty());

        roomService.deleteById(id);

        verify(roomRepository, times(0)).deleteById(id);
        assert (roomRepository.findById(id).isEmpty());

    }

}
