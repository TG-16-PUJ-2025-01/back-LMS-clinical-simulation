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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;
import co.edu.javeriana.lms.booking.services.RoomService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
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

        Room room1 = Room.builder().id(1L).name("Room A").type(mockRoomType).capacity(10).build();
        Room room2 = Room.builder().id(2L).name("Room B").type(mockRoomType).capacity(15).build();

        mockRoom = room1;

        List<Room> mockRooms = Arrays.asList(room1, room2);

        mockRoomsPage = new PageImpl<>(mockRooms, PageRequest.of(0, 10, Sort.by("name").ascending()), mockRooms.size());
    }

    @Test
    public void testSearchRooms() {
        // Arrange
        when(roomRepository.findByNameContaining("", mockRoomsPage.getPageable())).thenReturn(mockRoomsPage);

        // Act
        Page<Room> roomsPage = roomService.searchRooms("", 0, 10, "name", true);

        // Assert
        assert (roomsPage.getTotalElements() == mockRoomsPage.getTotalElements());
        assert (roomsPage.getContent().size() == mockRoomsPage.getContent().size());
        assert (roomsPage.getContent().equals(mockRoomsPage.getContent()));
        assert (roomsPage.getNumber() == mockRoomsPage.getNumber());
        assert (roomsPage.getSize() == mockRoomsPage.getSize());
    }

    @Test
    public void testSaveRoom_Success() {
        // Arrange
        when(roomRepository.findByName(mockRoom.getName())).thenReturn(null);
        when(roomTypeRepository.findById(mockRoomType.getId())).thenReturn(Optional.of(mockRoomType));
        when(roomRepository.save(mockRoom)).thenReturn(mockRoom);

        // Act
        Room savedRoom = roomService.save(mockRoom);

        // Assert
        assert (savedRoom.equals(mockRoom));
        verify(roomRepository, times(1)).save(mockRoom);
    }

    @Test
    public void testSaveRoom_NameConflict() {
        // Arrange
        when(roomRepository.findByName(mockRoom.getName())).thenReturn(mockRoom);

        // Act & Assert
        try {
            roomService.save(mockRoom);
        } catch (DataIntegrityViolationException e) {
            assert (e.getMessage().equals("El nombre de la sala ya existe"));
        }
        verify(roomRepository, times(0)).save(mockRoom);
    }

    @Test
    public void testSaveRoom_TypeNotFound() {
        // Arrange
        when(roomRepository.findByName(mockRoom.getName())).thenReturn(null);
        when(roomTypeRepository.findById(mockRoomType.getId())).thenReturn(Optional.empty());

        // Act & Assert
        try {
            roomService.save(mockRoom);
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Room type not found with id: " + mockRoomType.getId()));
        }
        verify(roomRepository, times(0)).save(mockRoom);
    }

    @Test
    public void testUpdateRoom_Success() {
        // Arrange
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.of(mockRoom));
        when(roomTypeRepository.findById(mockRoomType.getId())).thenReturn(Optional.of(mockRoomType));
        when(roomRepository.save(mockRoom)).thenReturn(mockRoom);

        // Act
        Room updatedRoom = roomService.update(mockRoom.getId(), mockRoom);

        // Assert
        assert (updatedRoom.equals(mockRoom));
        verify(roomRepository, times(1)).save(mockRoom);
    }

    @Test
    public void testUpdateRoom_NotFound() {
        // Arrange
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.empty());

        // Act & Assert
        try {
            roomService.update(mockRoom.getId(), mockRoom);
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Room not found with id: " + mockRoom.getId()));
        }
        verify(roomRepository, times(0)).save(mockRoom);
    }

    @Test
    public void testUpdateRoom_NameConflict() {
        // Arrange
        Room room1 = mockRoomsPage.getContent().get(0);
        Room room2 = mockRoomsPage.getContent().get(1);

        when(roomRepository.findById(room1.getId())).thenReturn(Optional.of(room1));
        when(roomRepository.findByName(room2.getName())).thenReturn(room2);
        when(roomTypeRepository.findById(mockRoomType.getId())).thenReturn(Optional.of(mockRoomType));

        // Act & Assert
        try {
            roomService.update(room1.getId(), room2); // Attempt to update room1 with room2's name
        } catch (DataIntegrityViolationException e) {
            assert (e.getMessage().equals("El nombre de la sala ya existe"));
        }

        // Verify that save is never called
        verify(roomRepository, times(0)).save(room2);
    }

    @Test
    public void testDeleteRoom_Success() {
        // Arrange
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.of(mockRoom));
        when(roomRepository.countByType(mockRoomType)).thenReturn(0L);

        // Act
        roomService.deleteById(mockRoom.getId());

        // Assert
        verify(roomRepository, times(1)).deleteById(mockRoom.getId());
        verify(roomTypeRepository, times(1)).delete(mockRoomType);
    }

    @Test
    public void testDeleteRoom_NotFound() {
        // Arrange
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.empty());

        // Act & Assert
        try {
            roomService.deleteById(mockRoom.getId());
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Room not found with id: " + mockRoom.getId()));
        }
        verify(roomRepository, times(0)).deleteById(mockRoom.getId());
    }

    @Test
    public void testFindAllTypes() {
        // Arrange
        when(roomTypeRepository.findAll()).thenReturn(Arrays.asList(mockRoomType));

        // Act
        List<RoomType> roomTypes = roomService.findAllTypes();

        // Assert
        assert (roomTypes.size() == 1);
        assert (roomTypes.get(0).equals(mockRoomType));
    }
}
