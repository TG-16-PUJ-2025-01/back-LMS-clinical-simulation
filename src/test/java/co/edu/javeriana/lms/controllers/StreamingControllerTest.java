package co.edu.javeriana.lms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.services.StreamingService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class StreamingControllerTest {

    @InjectMocks
    private StreamingController streamingController;

    @Mock
    private StreamingService streamingService;

    private InputStreamResource mockResource;

    @BeforeEach
    public void setUp() {
        mockResource = mock(InputStreamResource.class);
    }

    @Test
    public void testGetVideo() throws IOException {
        String title = "test.mp4";
        String range = "bytes=0-1024";

        when(streamingService.getVideo(title, range)).thenReturn(
                ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).header("Content-Range", range).body(mockResource));

        ResponseEntity<Resource> response = streamingController.getVideo(title, range);

        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("bytes=0-1024", response.getHeaders().getFirst("Content-Range"));
    }
}
