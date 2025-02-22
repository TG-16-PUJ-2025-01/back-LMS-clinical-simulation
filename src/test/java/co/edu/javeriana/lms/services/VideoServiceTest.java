package co.edu.javeriana.lms.services;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import co.edu.javeriana.lms.dtos.EditVideoDTO;
import co.edu.javeriana.lms.models.Video;
import co.edu.javeriana.lms.repositories.VideoRepository;

@SpringBootTest
public class VideoServiceTest {

    @InjectMocks
    private VideoService videoService;

    @Mock
    private VideoRepository videoRepository;

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
        when(videoRepository.findByNameContaining("", mockVideosPage.getPageable())).thenReturn(mockVideosPage);

        Page<Video> videosPage = videoService.searchVideos("", 0, 10, "name", true);

        assert (videosPage.getTotalElements() == mockVideosPage.getTotalElements());
        assert (videosPage.getContent().size() == mockVideosPage.getContent().size());
        assert (videosPage.getContent().equals(mockVideosPage.getContent()));
        assert (videosPage.getNumberOfElements() == mockVideosPage.getNumberOfElements());
        assert (videosPage.getTotalPages() == mockVideosPage.getTotalPages());
        assert (videosPage.getTotalElements() == mockVideosPage.getTotalElements());
        assert (videosPage.getNumber() == mockVideosPage.getNumber());
        assert (videosPage.getSize() == mockVideosPage.getSize());
    }

    @Test
    public void testEditVideo() {
        Long id = 1L;
        when(videoRepository.findById(id)).thenReturn(java.util.Optional.of(mockVideo));
        when(videoRepository.save(mockVideo)).thenReturn(mockVideo);

        Video editedVideo = videoService.editVideo(id,
                new EditVideoDTO(mockVideo.getName(), mockVideo.getExpirationDate()));

        assert (editedVideo.equals(mockVideo));
    }

    @Test
    public void testDeleteVideos() {
        Long id = 1L;
        when(videoRepository.findById(id)).thenReturn(java.util.Optional.of(mockVideo));

        Video deletedVideo = videoService.deleteVideo(id);

        assert (deletedVideo.equals(mockVideo));
    }
}
