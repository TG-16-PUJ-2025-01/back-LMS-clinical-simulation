package co.edu.javeriana.lms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.RoomDto;
import co.edu.javeriana.lms.dtos.RoomTypeDto;
import co.edu.javeriana.lms.models.Room;
import co.edu.javeriana.lms.models.RoomType;
import co.edu.javeriana.lms.services.RoomService;
import co.edu.javeriana.lms.services.RoomTypeService;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomService roomService;

    @Mock
    private RoomTypeService roomTypeService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllRooms() {
        // Arrange
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        RoomType roomType2 = new RoomType();
        roomType2.setName("Urgencias");

        List<Room> rooms = new ArrayList<>();

        Room roomA = new Room();
        roomA.setId(1L);
        roomA.setName("Room A");
        roomA.setType(roomType1);
        rooms.add(roomA);

        Room roomB = new Room();
        roomB.setId(2L);
        roomB.setName("Room B");
        roomB.setType(roomType2);
        rooms.add(roomB);

        Page<Room> roomPage = new PageImpl<>(rooms, PageRequest.of(0, 10), rooms.size());

        when(roomService.searchRooms("", 0, 10, "id", true)).thenReturn(roomPage);
        when(request.getHeader("Host")).thenReturn("localhost:8080");
        when(request.getScheme()).thenReturn("http");

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.getAllRooms(0, 10, "id", true, "", request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("ok", response.getBody().getMessage());
        assertEquals(2, ((List<?>) response.getBody().getData()).size());
        verify(roomService, times(1)).searchRooms("", 0, 10, "id", true);
    }

    @Test
    public void testGetRoomById_Success() {
        // Arrange
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        Room roomA = new Room();
        roomA.setId(1L);
        roomA.setName("Room A");
        roomA.setType(roomType1);

        when(roomService.findById(1L)).thenReturn(java.util.Optional.of(roomA));

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.getRoomById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room found", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(roomService, times(1)).findById(1L);
    }

    @Test
    public void testGetRoomById_NotFound() {
        // Arrange
        when(roomService.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.getRoomById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room not found", response.getBody().getMessage());
        verify(roomService, times(1)).findById(1L);
    }

    @Test
    public void testGetAllRoomsTypes() {
        // Arrange
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        RoomType roomType2 = new RoomType();
        roomType2.setName("Urgencias");

        List<RoomType> roomTypes = new ArrayList<>();
        roomTypes.add(roomType1);
        roomTypes.add(roomType2);

        when(roomService.findAllTypes()).thenReturn(roomTypes);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.getRoomTypes();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room types found", response.getBody().getMessage());
        assertEquals(2, ((List<?>) response.getBody().getData()).size());
        verify(roomService, times(1)).findAllTypes();
    }

    @Test
    public void testDeleteRoom_Success() {
        // Arrange
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        Room roomA = new Room();
        roomA.setId(1L);
        roomA.setName("Room A");
        roomA.setType(roomType1);

        when(roomService.findById(1L)).thenReturn(java.util.Optional.of(roomA));

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.deleteRoomById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room deleted successfully", response.getBody().getMessage());
        verify(roomService, times(1)).findById(1L);
        verify(roomService, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteRoom_NotFound() {
        // Arrange
        when(roomService.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.deleteRoomById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room not found", response.getBody().getMessage());
        verify(roomService, times(1)).findById(1L);
    }

    @Test
    public void testUpdateRoom_Success() {
        // Arrange
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        Room roomA = new Room();
        roomA.setId(1L);
        roomA.setName("Room A");
        roomA.setType(roomType1);

        Room roomB = new Room();
        roomB.setId(1L);
        roomB.setName("Room B");
        roomB.setType(roomType1);

        when(roomService.findById(1L)).thenReturn(java.util.Optional.of(roomA));
        when(roomService.update(roomA)).thenReturn(roomB);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.updateRoom(roomA);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room updated successfully", response.getBody().getMessage());
        verify(roomService, times(1)).findById(1L);
        verify(roomService, times(1)).update(roomA);
    }

    @Test
    public void testUpdateRoom_NotFound() {
        // Arrange
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        Room roomA = new Room();
        roomA.setId(1L);
        roomA.setName("Room A");
        roomA.setType(roomType1);

        when(roomService.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.updateRoom(roomA);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room not found", response.getBody().getMessage());
        verify(roomService, times(1)).findById(1L);
    }

    @Test
    public void testUpdateRoom_NameConflict() {
        // Arrange
        RoomType roomType1 = new RoomType();
        roomType1.setName("Cirugia");

        Room roomA = new Room();
        roomA.setId(1L);
        roomA.setName("Room A");
        roomA.setType(roomType1);

        // Simula que la sala exista
        when(roomService.findById(1L)).thenReturn(java.util.Optional.of(roomA));
        // Simula el comportamiento del servicio para lanzar la excepción por conflicto
        // de nombre
        when(roomService.update(roomA)).thenThrow(new IllegalArgumentException("El nombre de la sala ya existe"));

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.updateRoom(roomA);

        // Assert
        assertNotNull(response);
        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("El nombre de la sala ya existe", response.getBody().getMessage());
        verify(roomService, times(1)).findById(1L);
        verify(roomService, times(1)).update(roomA);
    }

    @Test
    public void testAddRoom_Success() {
        // Arrange
        RoomTypeDto roomTypeDto = new RoomTypeDto();
        roomTypeDto.setName("Cirugia");

        RoomDto roomDto = new RoomDto();
        roomDto.setName("Room A");
        roomDto.setType(roomTypeDto);

        RoomType roomType = new RoomType();
        roomType.setName("Cirugia");

        Room roomA = new Room();
        roomA.setId(null);
        roomA.setName("Room A");
        roomA.setType(roomType);

        when(roomService.save(roomA)).thenReturn(roomA);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.addRoom(roomDto);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room created successfully", response.getBody().getMessage());
        verify(roomService, times(1)).save(roomA);
    }

    @Test
    public void testAddRoomType_Success() {
        // Arrange
        RoomType roomType = new RoomType();
        roomType.setName("Cirugia");

        when(roomTypeService.save(roomType)).thenReturn(roomType);

        // Act
        ResponseEntity<ApiResponseDto<?>> response = roomController.addRoomType(roomType);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Room type created successfully", response.getBody().getMessage());
        verify(roomTypeService, times(1)).save(roomType);
    }

}
