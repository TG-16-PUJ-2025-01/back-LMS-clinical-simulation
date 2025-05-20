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

    private static Room mockRoom1;
    private static Room mockRoom2;
    private static RoomType mockRoomType;
    private static Page<Room> mockRoomsPage;

    @BeforeAll
    public static void setUpAll() {
        // Mock Room Type
        mockRoomType = RoomType.builder().id(1L).name("Cirugia").build();

        // Mock Rooms
        mockRoom1 = Room.builder().id(1L).name("Room A").capacity(20).ip("10.43.100.23").type(mockRoomType).build();
        mockRoom2 = Room.builder().id(2L).name("Room B").capacity(15).ip("10.41.104.12").type(mockRoomType).build();

        List<Room> mockRooms = Arrays.asList(mockRoom1, mockRoom2);

        mockRoomsPage = new PageImpl<>(mockRooms, PageRequest.of(0, 10, Sort.by("name").ascending()), mockRooms.size());
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
        when(roomRepository.findByName(mockRoom1.getName())).thenReturn(null);
        when(roomTypeRepository.findById(mockRoomType.getId())).thenReturn(Optional.of(mockRoomType));
        when(roomRepository.save(mockRoom1)).thenReturn(mockRoom1);

        // Act
        Room savedRoom = roomService.save(mockRoom1);

        // Assert
        assert (savedRoom.equals(mockRoom1));
        verify(roomRepository, times(1)).save(mockRoom1);
    }

    @Test
    public void testSaveRoom_NameConflict() {
        // Arrange
        when(roomRepository.findByName(mockRoom1.getName())).thenReturn(mockRoom1);

        // Act & Assert
        try {
            roomService.save(mockRoom1);
        } catch (DataIntegrityViolationException e) {
            assert (e.getMessage().equals("El nombre de la sala ya existe"));
        }
        verify(roomRepository, times(0)).save(mockRoom1);
    }

    @Test
    public void testSaveRoom_TypeNotFound() {
        // Arrange
        when(roomRepository.findByName(mockRoom1.getName())).thenReturn(null);
        when(roomTypeRepository.findById(mockRoomType.getId())).thenReturn(Optional.empty());

        // Act & Assert
        try {
            roomService.save(mockRoom1);
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Room type not found with id: " + mockRoomType.getId()));
        }
        verify(roomRepository, times(0)).save(mockRoom1);
    }

    @Test
    public void testUpdateRoom_Success() {
        // Arrange
        when(roomRepository.findById(mockRoom1.getId())).thenReturn(Optional.of(mockRoom1));
        when(roomTypeRepository.findById(mockRoomType.getId())).thenReturn(Optional.of(mockRoomType));
        when(roomRepository.save(mockRoom1)).thenReturn(mockRoom1);

        // Act
        Room updatedRoom = roomService.update(mockRoom1.getId(), mockRoom1);

        // Assert
        assert (updatedRoom.equals(mockRoom1));
        verify(roomRepository, times(1)).save(mockRoom1);
    }

    @Test
    public void testUpdateRoom_NotFound() {
        // Arrange
        when(roomRepository.findById(mockRoom1.getId())).thenReturn(Optional.empty());

        // Act & Assert
        try {
            roomService.update(mockRoom1.getId(), mockRoom1);
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Room not found with id: " + mockRoom1.getId()));
        }
        verify(roomRepository, times(0)).save(mockRoom1);
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
        when(roomRepository.findById(mockRoom1.getId())).thenReturn(Optional.of(mockRoom1));
        when(roomRepository.countByType(mockRoomType)).thenReturn(0L);

        // Act
        roomService.deleteById(mockRoom1.getId());

        // Assert
        verify(roomRepository, times(1)).deleteById(mockRoom1.getId());
        verify(roomTypeRepository, times(1)).delete(mockRoomType);
    }

    @Test
    public void testDeleteRoom_NotFound() {
        // Arrange
        when(roomRepository.findById(mockRoom1.getId())).thenReturn(Optional.empty());

        // Act & Assert
        try {
            roomService.deleteById(mockRoom1.getId());
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Room not found with id: " + mockRoom1.getId()));
        }
        verify(roomRepository, times(0)).deleteById(mockRoom1.getId());
    }

    @Test
    public void testFindById_Success() {
        // Arrange
        when(roomRepository.findById(mockRoom1.getId())).thenReturn(Optional.of(mockRoom1));

        // Act
        Room foundRoom = roomService.findById(mockRoom1.getId());

        // Assert
        assert (foundRoom != null);
        assert (foundRoom.equals(mockRoom1));
        verify(roomRepository, times(1)).findById(mockRoom1.getId());
    }

    @Test
    public void testFindById_NotFound() {
        // Arrange
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            roomService.findById(999L);
            assert false; // Should not reach here
        } catch (EntityNotFoundException e) {
            assert (e.getMessage().equals("Room not found with id: 999"));
        }
        verify(roomRepository, times(1)).findById(999L);
    }

    @Test
    public void testFindByName_Found() {
        // Arrange
        when(roomRepository.findByName(mockRoom1.getName())).thenReturn(mockRoom1);

        // Act
        Room foundRoom = roomService.findByName(mockRoom1.getName());

        // Assert
        assert (foundRoom != null);
        assert (foundRoom.equals(mockRoom1));
        verify(roomRepository, times(1)).findByName(mockRoom1.getName());
    }

    @Test
    public void testFindByName_NotFound() {
        // Arrange
        when(roomRepository.findByName("NonExistentRoom")).thenReturn(null);

        // Act
        Room foundRoom = roomService.findByName("NonExistentRoom");

        // Assert
        assert (foundRoom == null);
        verify(roomRepository, times(1)).findByName("NonExistentRoom");
    }

    @Test
    public void testExistsByName_True() {
        // Arrange
        when(roomRepository.findByName(mockRoom1.getName())).thenReturn(mockRoom1);

        // Act
        boolean exists = roomService.existsByName(mockRoom1.getName());

        // Assert
        assert (exists);
        verify(roomRepository, times(1)).findByName(mockRoom1.getName());
    }

    @Test
    public void testExistsByName_False() {
        // Arrange
        when(roomRepository.findByName("NonExistentRoom")).thenReturn(null);

        // Act
        boolean exists = roomService.existsByName("NonExistentRoom");

        // Assert
        assert (!exists);
        verify(roomRepository, times(1)).findByName("NonExistentRoom");
    }

    @Test
    public void testFindRoomTypeByName_Found() {
        // Arrange
        when(roomTypeRepository.findByName(mockRoomType.getName())).thenReturn(mockRoomType);

        // Act
        RoomType foundType = roomService.findRoomTypeByName(mockRoomType.getName());

        // Assert
        assert (foundType != null);
        assert (foundType.equals(mockRoomType));
        verify(roomTypeRepository, times(1)).findByName(mockRoomType.getName());
    }

    @Test
    public void testFindRoomTypeByName_NotFound() {
        // Arrange
        when(roomTypeRepository.findByName("NonExistentType")).thenReturn(null);

        // Act
        RoomType foundType = roomService.findRoomTypeByName("NonExistentType");

        // Assert
        assert (foundType == null);
        verify(roomTypeRepository, times(1)).findByName("NonExistentType");
    }

    @Test
    public void testSaveRoomType() {
        // Arrange
        RoomType newType = RoomType.builder().id(2L).name("Simulación").build();
        when(roomTypeRepository.save(newType)).thenReturn(newType);

        // Act
        RoomType savedType = roomService.saveRoomType(newType);

        // Assert
        assert (savedType != null);
        assert (savedType.equals(newType));
        verify(roomTypeRepository, times(1)).save(newType);
    }

    @Test
    public void testFindAllTypes() {
        // Arrange
        when(roomTypeRepository.findAll()).thenReturn(Arrays.asList(mockRoomType));

        // Act
        List<RoomType> types = roomService.findAllTypes();

        // Assert
        assert (types.size() == 1);
        assert (types.get(0).equals(mockRoomType));
        verify(roomTypeRepository, times(1)).findAll();
    }

    @Test
    public void testCountByType() {
        // Arrange
        when(roomRepository.countByType(mockRoomType)).thenReturn(2L);

        // Act
        long count = roomService.countByType(mockRoomType);

        // Assert
        assert (count == 2L);
        verify(roomRepository, times(1)).countByType(mockRoomType);
    }
}
