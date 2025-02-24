package co.edu.javeriana.lms.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class StreamingServiceTest {

    @InjectMocks
    private StreamingService streamingService;

    private MockedStatic<Files> filesMock;
    private MockedStatic<Paths> pathsMock;
    private Path mockPath;
    private File mockFile;

    @BeforeEach
    public void setUp() {
        filesMock = mockStatic(Files.class);
        pathsMock = mockStatic(Paths.class);
        mockPath = mock(Path.class);
        mockFile = mock(File.class);
        pathsMock.when(() -> Paths.get(org.mockito.ArgumentMatchers.anyString())).thenReturn(mockPath);
    }

    @AfterEach
    public void tearDown() {
        filesMock.close();
        pathsMock.close();
    }

    @Test
    public void testGetVideoExistsWithRange() throws IOException {
        String title = "test.mp4";
        String range = "bytes=0-1024";

        when(mockPath.toFile()).thenReturn(mockFile);
        filesMock.when(() -> Files.exists(mockPath)).thenReturn(true);
        filesMock.when(() -> Files.size(mockPath)).thenReturn(2048L);

        try (MockedConstruction<RandomAccessFile> randomAccessFileMock = mockConstruction(RandomAccessFile.class)) {
            mock(RandomAccessFile.class);

            ResponseEntity<Resource> response = streamingService.getVideo(title, range);

            assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("bytes 0-1024/2048", response.getHeaders().getFirst("Content-Range"));
        }
    }

    @Test
    public void testGetVideoExistsWithoutRange() throws IOException {
        String title = "test.mp4";
        String range = null;

        when(mockPath.toFile()).thenReturn(mockFile);
        System.out.println("HEREEE");
        System.out.println(mockFile);
        System.out.println("HEREEE");
        System.out.println(mockPath.toFile());
        filesMock.when(() -> Files.exists(mockPath)).thenReturn(true);
        filesMock.when(() -> Files.size(mockPath)).thenReturn(2048L);

        try (MockedConstruction<RandomAccessFile> randomAccessFileMock = mockConstruction(RandomAccessFile.class)) {
            mock(RandomAccessFile.class);

            ResponseEntity<Resource> response = streamingService.getVideo(title, range);
    
            assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("bytes 0-2047/2048", response.getHeaders().getFirst("Content-Range"));
        }

    }

    @Test
    public void testGetVideoNotExists() throws IOException {
        String title = "nonexistent.mp4";
        String range = "bytes=0-1024";

        filesMock.when(() -> Files.exists(mockPath)).thenReturn(false);

        ResponseEntity<Resource> response = streamingService.getVideo(title, range);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
