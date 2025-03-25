package co.edu.javeriana.lms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.booking.controllers.RoomController;
import co.edu.javeriana.lms.booking.dtos.RoomDto;
import co.edu.javeriana.lms.booking.dtos.RoomTypeDto;
import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.services.RoomService;
import co.edu.javeriana.lms.booking.services.RoomTypeService;
import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;

@SpringBootTest
@ActiveProfiles("test")
public class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomService roomService;

    @Mock
    private RoomTypeService roomTypeService;

    private static RoomType roomType1;
    private static Room roomA;
    private static List<Room> rooms;
    private static List<RoomType> roomTypes;
    private static RoomDto roomDto;
    private static Page<Room> roomPage;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setUpAll() {
        roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        roomA = new Room();
        roomA.setId(1L);
        roomA.setName("Room A");
        roomA.setType(roomType1);
        roomA.setCapacity(10);

        rooms = new ArrayList<>();
        rooms.add(roomA);

        roomDto = new RoomDto();
        roomDto.setName(roomA.getName());
        roomDto.setTypeId(1L);
        roomDto.setCapacity(roomA.getCapacity());

        roomTypes = new ArrayList<>();
        roomTypes.add(roomType1);

        roomPage = new PageImpl<>(rooms, PageRequest.of(0, 10), rooms.size());
    }

    @Test
    public void testGetAllRooms() {
        // Arrange
        when(roomService.searchRooms("", 0, 10, "id", true)).thenReturn(roomPage);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.getAllRooms(0, 10, "id", true, "");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Rooms retrieved successfully", response.getBody().getMessage());
        assertEquals(1, ((List<?>) response.getBody().getData()).size());
        verify(roomService, times(1)).searchRooms("", 0, 10, "id", true);
    }

    @Test
    public void testGetRoomById_Success() {
        // Arrange
        when(roomService.findById(1L)).thenReturn(roomA);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.getRoomById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room retrieved successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(roomService, times(1)).findById(1L);
    }

    @Test
    public void testGetAllRoomsTypes() {
        // Arrange
        when(roomService.findAllTypes()).thenReturn(roomTypes);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.getRoomTypes();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room types retrieved successfully", response.getBody().getMessage());
        assertEquals(1, ((List<?>) response.getBody().getData()).size());
        verify(roomService, times(1)).findAllTypes();
    }

    @Test
    public void testDeleteRoom_Success() {
        // Arrange
        when(roomService.findById(1L)).thenReturn(roomA);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.deleteRoomById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room deleted successfully", response.getBody().getMessage());
        verify(roomService, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateRoom_Success() {
        // Arrange
        when(roomService.update(1L, roomDto.toEntity())).thenReturn(roomA);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.updateRoom(roomA.getId(), roomDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room updated successfully", response.getBody().getMessage());
        verify(roomService, times(1)).update(1L, roomDto.toEntity());
    }

    @Test
    public void testAddRoom_Success() {
        // Arrange
        when(roomService.save(roomDto.toEntity())).thenReturn(roomA);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.addRoom(roomDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room created successfully", response.getBody().getMessage());
        verify(roomService, times(1)).save(roomDto.toEntity());
    }

    @Test
    public void testAddRoomType_Success() {
        // Arrange
        when(roomTypeService.save(roomType1)).thenReturn(roomType1);

        // Act
        RoomTypeDto roomTypeDto = new RoomTypeDto();
        roomTypeDto.setName(roomType1.getName());
        ResponseEntity<ApiResponseDto<?>> response = roomController.addRoomType(roomTypeDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room type created successfully", response.getBody().getMessage());
        verify(roomTypeService, times(1)).save(roomType1);
    }
}
