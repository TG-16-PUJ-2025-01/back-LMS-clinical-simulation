package co.edu.javeriana.lms.controllers;

import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.EditVideoDTO;
import co.edu.javeriana.lms.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.models.Video;
import co.edu.javeriana.lms.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;

public class VideoControllerTest {
    @InjectMocks
    private VideoController videoController;

    @Mock
    private VideoService videoService;

    @Mock
    private HttpServletRequest request;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static Page<Video> mockVideosPage;

    private static Video mockVideo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setUpAll() throws ParseException {
        Video video1 = Video.builder().name("Video 1").recordingDate(dateFormat.parse("2025-02-22"))
                .expirationDate(new Date()).duration(62L).size(8.3).build();

        Video video2 = Video.builder().name("Video 2").recordingDate(dateFormat.parse("2024-12-24"))
                .expirationDate(new Date()).duration(3600L).size(10.3).build();

        mockVideo = Video.builder().name("Video 1").recordingDate(dateFormat.parse("2025-02-22"))
                .expirationDate(new Date()).duration(62L).size(8.3).build();

        List<Video> mockVideos = Arrays.asList(video1, video2);

        mockVideosPage = new PageImpl<>(Arrays.asList(video1, video2),
                PageRequest.of(0, 10, Sort.by("name").ascending()), mockVideos.size());
    }

    @Test
    public void testSearchVideos() {
        when(videoService.searchVideos("", 0, 10, "name", true)).thenReturn(mockVideosPage);
        when(request.getHeader("Host")).thenReturn("localhost:8080");
        when(request.getScheme()).thenReturn("http");

        ResponseEntity<ApiResponseDto<List<Video>>> videosPage = videoController.searchVideos(0, 10, "name", true, "", request);

        PaginationMetadataDto metadata = (PaginationMetadataDto) videosPage.getBody().getMetadata();
        
        assert (videosPage.getBody().getData().equals(mockVideosPage.getContent()));
        assert (metadata.getTotal() == mockVideosPage.getTotalElements());
        assert (metadata.getSize() == mockVideosPage.getNumberOfElements());
        assert (metadata.getTotalPages() == mockVideosPage.getTotalPages());
        assert (metadata.getPage() == mockVideosPage.getNumber());
        assert (metadata.getPrevious() == null);
        assert (metadata.getNext() == null);
    }

    @Test
    public void testEditVideo() {
        Long id = 1L;
        when(videoService.editVideo(id, new EditVideoDTO(mockVideo.getName(), mockVideo.getExpirationDate()))).thenReturn(mockVideo);

        ResponseEntity<ApiResponseDto<Video>> editedVideo = videoController.editVideo(id, new EditVideoDTO(mockVideo.getName(), mockVideo.getExpirationDate()));

        assert (editedVideo.getBody().getData().equals(mockVideo));
        assert (editedVideo.getBody().getMetadata() == null);
    }

    @Test
    public void testDeleteVideos() {
        Long id = 1L;
        when(videoService.deleteVideo(id)).thenReturn(mockVideo);

        ResponseEntity<ApiResponseDto<Video>> deletedVideo = videoController.deleteVideo(id);

        assert (deletedVideo.getBody().getData().equals(mockVideo));
        assert (deletedVideo.getBody().getMetadata() == null);
    }
}
