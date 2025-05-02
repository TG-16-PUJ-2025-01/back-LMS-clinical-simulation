package co.edu.javeriana.lms.controllers;

import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.shared.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.videos.controllers.VideoController;
import co.edu.javeriana.lms.videos.dtos.EditVideoDto;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.services.VideoService;

@SpringBootTest
@ActiveProfiles("test")
public class VideoControllerTest {
    @InjectMocks
    private VideoController videoController;

    @Mock
    private VideoService videoService;

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
                .duration(62L).size(8.3).build();

        Video video2 = Video.builder().name("Video 2").recordingDate(dateFormat.parse("2024-12-24"))
                .duration(3600L).size(10.3).build();

        mockVideo = Video.builder().name("Video 1").recordingDate(dateFormat.parse("2025-02-22"))
                .duration(62L).size(8.3).build();

        List<Video> mockVideos = Arrays.asList(video1, video2);

        mockVideosPage = new PageImpl<>(Arrays.asList(video1, video2),
                PageRequest.of(0, 10, Sort.by("name").ascending()), mockVideos.size());
    }

    @Test
    public void testSearchVideos() {
        when(videoService.searchVideos("", 0, 10, "name", true)).thenReturn(mockVideosPage);

        ResponseEntity<ApiResponseDto<List<Video>>> videosPage = videoController.searchVideos(0, 10, "name", true, "");

        PaginationMetadataDto metadata = (PaginationMetadataDto) videosPage.getBody().getMetadata();
        
        assert (videosPage.getBody().getData().equals(mockVideosPage.getContent()));
        assert (metadata.getTotal() == mockVideosPage.getTotalElements());
        assert (metadata.getSize() == mockVideosPage.getNumberOfElements());
        assert (metadata.getTotalPages() == mockVideosPage.getTotalPages());
        assert (metadata.getPage() == mockVideosPage.getNumber());
    }

    @Test
    public void testSearchVideosMultiplePages() {
        Page<Video> filteredVideosPage = new PageImpl<>(mockVideosPage.getContent(), PageRequest.of(0, 1, Sort.by("name").ascending()), mockVideosPage.getTotalElements());

        when(videoService.searchVideos("", 0, 1, "name", true)).thenReturn(filteredVideosPage);

        ResponseEntity<ApiResponseDto<List<Video>>> videosPage = videoController.searchVideos(0, 1, "name", true, "");

        PaginationMetadataDto metadata = (PaginationMetadataDto) videosPage.getBody().getMetadata();
        
        assert (videosPage.getBody().getData().equals(filteredVideosPage.getContent()));
        assert (metadata.getTotal() == filteredVideosPage.getTotalElements());
        assert (metadata.getSize() == filteredVideosPage.getNumberOfElements());
        assert (metadata.getTotalPages() == filteredVideosPage.getTotalPages());
        assert (metadata.getPage() == filteredVideosPage.getNumber());
    }

    @Test
    public void testEditVideoSuccess() {
        Long id = 1L;
        when(videoService.editVideo(id, new EditVideoDto(mockVideo.getName()))).thenReturn(mockVideo);

        ResponseEntity<ApiResponseDto<Video>> editedVideo = videoController.editVideo(id, new EditVideoDto(mockVideo.getName()));

        assert (editedVideo.getBody().getData().equals(mockVideo));
        assert (editedVideo.getBody().getMetadata() == null);
    }

    @Test
    public void testEditVideoFailure() {
        Long id = 1L;
        when(videoService.editVideo(id, new EditVideoDto(mockVideo.getName()))).thenReturn(null);

        ResponseEntity<ApiResponseDto<Video>> editedVideo = videoController.editVideo(id, new EditVideoDto(mockVideo.getName()));

        assert (editedVideo.getBody().getData() == null);
        assert (editedVideo.getStatusCode() == HttpStatusCode.valueOf(404));
        assert (editedVideo.getBody().getStatus() == 404);
        assert (editedVideo.getBody().getMetadata() == null);
    }

    @Test
    public void testDeleteVideosSuccess() {
        Long id = 1L;
        when(videoService.deleteVideo(id)).thenReturn(mockVideo);

        ResponseEntity<ApiResponseDto<Video>> deletedVideo = videoController.deleteVideo(id);

        assert (deletedVideo.getBody().getData().equals(mockVideo));
        assert (deletedVideo.getBody().getMetadata() == null);
    }

    @Test
    public void testDeleteVideosFailure() {
        Long id = 1L;
        when(videoService.deleteVideo(id)).thenReturn(null);

        ResponseEntity<ApiResponseDto<Video>> deletedVideo = videoController.deleteVideo(id);

        assert (deletedVideo.getBody().getData() == null);
        assert (deletedVideo.getStatusCode() == HttpStatusCode.valueOf(404));
        assert (deletedVideo.getBody().getStatus() == 404);
        assert (deletedVideo.getBody().getMetadata() == null);
    }
}
