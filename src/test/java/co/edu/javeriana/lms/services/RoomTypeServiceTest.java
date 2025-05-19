package co.edu.javeriana.lms.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.booking.models.RoomType;
import co.edu.javeriana.lms.booking.repositories.RoomTypeRepository;
import co.edu.javeriana.lms.booking.services.RoomTypeService;


@SpringBootTest
@ActiveProfiles("test")
public class RoomTypeServiceTest {

    @InjectMocks
    private RoomTypeService roomTypeService;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    private static RoomType roomType;
    private static RoomType roomType2;
    private static List<RoomType> roomTypes;

    @BeforeEach
    public void setUpAll(){
        MockitoAnnotations.openMocks(this);

        // Mock RoomType objects
        roomType = RoomType.builder()
                .id(1L)
                .name("Room Type 1")
                .build();

        roomType2 = RoomType.builder()
                .id(2L)
                .name("Room Type 2")
                .build();
        
        // Mock list of RoomType objects
        roomTypes = List.of(roomType, roomType2);
    }

    @Test
    public void testSave_Success() {
        // Arrange
        when(roomTypeRepository.findByName(roomType.getName())).thenReturn(null);
        when(roomTypeRepository.save(roomType)).thenReturn(roomType);

        // Act
        RoomType saved = roomTypeService.save(roomType);

        // Assert
        assertNotNull(saved);
        assertEquals(roomType.getName(), saved.getName());
        verify(roomTypeRepository, times(1)).findByName(roomType.getName());
        verify(roomTypeRepository, times(1)).save(roomType);
    }

    @Test
    public void testSave_EmptyName() {
        // Arrange
        RoomType emptyNameType = RoomType.builder().id(3L).name("").build();

        // Act & Assert
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            roomTypeService.save(emptyNameType);
        });
        assertTrue(exception.getMessage().contains("no puede estar vacio"));
        verify(roomTypeRepository, times(0)).findByName("");
        verify(roomTypeRepository, times(0)).save(emptyNameType);
    }

    @Test
    public void testSave_DuplicateName() {
        // Arrange
        when(roomTypeRepository.findByName(roomType.getName())).thenReturn(roomType);

        // Act & Assert
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            roomTypeService.save(roomType);
        });
        assertTrue(exception.getMessage().contains("El nombre del tipo de sala ya existe"));
        verify(roomTypeRepository, times(1)).findByName(roomType.getName());
        verify(roomTypeRepository, times(0)).save(roomType);
    }

    @Test
    public void testFindById_Found() {
        // Arrange
        when(roomTypeRepository.findById(roomType.getId())).thenReturn(Optional.of(roomType));

        // Act
        RoomType found = roomTypeService.findById(roomType.getId());

        // Assert
        assertNotNull(found);
        assertEquals(roomType.getName(), found.getName());
        verify(roomTypeRepository, times(1)).findById(roomType.getId());
    }

    @Test
    public void testFindById_NotFound() {
        // Arrange
        when(roomTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            roomTypeService.findById(999L);
        });
        assertTrue(exception.getMessage().contains("Room Type with 999 not found"));
        verify(roomTypeRepository, times(1)).findById(999L);
    }

    @Test
    public void testFindAll() {
        // Arrange
        when(roomTypeRepository.findAll()).thenReturn(roomTypes);

        // Act
        List<RoomType> result = roomTypeService.findAll(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roomTypeRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteById() {
        // Arrange
        doNothing().when(roomTypeRepository).deleteById(roomType.getId());
        when(roomTypeRepository.findById(roomType.getId())).thenReturn(Optional.of(roomType));

        // Act
        roomTypeService.deleteById(roomType.getId());

        // Assert
        verify(roomTypeRepository, times(1)).deleteById(roomType.getId());
    }

    @Test
    public void testDeleteById_NotFound() {
        // Arrange
        when(roomTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            roomTypeService.deleteById(999L);
        });
        assertTrue(exception.getMessage().contains("Room Type with 999 not found"));
        verify(roomTypeRepository, times(1)).findById(999L);
    }
}
