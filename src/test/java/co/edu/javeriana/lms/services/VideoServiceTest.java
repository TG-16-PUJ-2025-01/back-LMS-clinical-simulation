package co.edu.javeriana.lms.services;

import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import co.edu.javeriana.lms.videos.dtos.EditVideoDto;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import co.edu.javeriana.lms.videos.services.VideoService;

@SpringBootTest
@ActiveProfiles("test")
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
                .duration(62L).size(8.3).videoUrl("/streaming/video/test1.mp4").build();

        Video video2 = Video.builder().name("Video 2").recordingDate(dateFormat.parse("2024-12-24"))
                .duration(3600L).size(10.3).videoUrl("/streaming/video/test2.mp4").build();

        mockVideo = Video.builder().name("Video 1").recordingDate(dateFormat.parse("2025-02-22"))
                .duration(62L).size(8.3).videoUrl("/streaming/video/test3.mp4").build();

        List<Video> mockVideos = Arrays.asList(video1, video2);

        mockVideosPage = new PageImpl<>(Arrays.asList(video1, video2),
                PageRequest.of(0, 10, Sort.by("name").ascending()), mockVideos.size());
    }

    @Test
    public void testSearchVideos() {
        when(videoRepository.findByNameContainingIgnoreCase("", mockVideosPage.getPageable()))
                .thenReturn(mockVideosPage);

        Page<Video> videosPage = videoService.searchVideos("", 0, 10, "name", true);

        assert (videosPage.getTotalElements() == mockVideosPage.getTotalElements());
        assert (videosPage.getContent().size() == mockVideosPage.getContent().size());
        assert (videosPage.getContent().equals(mockVideosPage.getContent()));
        assert (videosPage.getNumberOfElements() == mockVideosPage.getNumberOfElements());
        assert (videosPage.getTotalPages() == mockVideosPage.getTotalPages());
        assert (videosPage.getTotalElements() == mockVideosPage.getTotalElements());
        assert (videosPage.getNumber() == mockVideosPage.getNumber());
        assert (videosPage.getSize() == mockVideosPage.getSize());
        assert (!videosPage.hasPrevious());
        assert (!videosPage.hasNext());
    }

    @Test
    public void testSearchVideosMultiplePages() {
        Page<Video> filteredVideosPage = new PageImpl<>(mockVideosPage.getContent(),
                PageRequest.of(0, 1, Sort.by("name").ascending()), mockVideosPage.getTotalElements());

        when(videoRepository.findByNameContainingIgnoreCase("", PageRequest.of(0, 1, Sort.by("name").ascending())))
                .thenReturn(filteredVideosPage);

        Page<Video> videosPage = videoService.searchVideos("", 0, 1, "name", true);

        assert (videosPage.getTotalElements() == filteredVideosPage.getTotalElements());
        assert (videosPage.getContent().size() == filteredVideosPage.getContent().size());
        assert (videosPage.getContent().equals(filteredVideosPage.getContent()));
        assert (videosPage.getNumberOfElements() == filteredVideosPage.getNumberOfElements());
        assert (videosPage.getTotalPages() == filteredVideosPage.getTotalPages());
        assert (videosPage.getTotalElements() == filteredVideosPage.getTotalElements());
        assert (videosPage.getNumber() == filteredVideosPage.getNumber());
        assert (videosPage.getSize() == filteredVideosPage.getSize());
        assert (!videosPage.hasPrevious());
        assert (videosPage.hasNext());
    }

    @Test
    public void testEditVideoSuccess() {
        Long id = 1L;
        when(videoRepository.findById(id)).thenReturn(Optional.of(mockVideo));
        when(videoRepository.save(mockVideo)).thenReturn(mockVideo);

        Video editedVideo = videoService.editVideo(id,
                new EditVideoDto(mockVideo.getName()));

        assert (editedVideo.equals(mockVideo));
    }

    @Test
    public void testEditVideoFailure() {
        Long id = 1L;
        when(videoRepository.findById(id)).thenReturn(Optional.empty());

        Video editedVideo = videoService.editVideo(id,
                new EditVideoDto(mockVideo.getName()));

        assert (editedVideo == null);
    }

    @Test
    public void testDeleteVideosSuccess() {
        Long id = 1L;
        when(videoRepository.findById(id)).thenReturn(Optional.of(mockVideo));

        Video deletedVideo = videoService.deleteVideo(id);

        assert (deletedVideo.equals(mockVideo));
    }

    @Test
    public void testDeleteVideosFailure() {
        Long id = 1L;
        when(videoRepository.findById(id)).thenReturn(Optional.empty());

        Video deletedVideo = videoService.deleteVideo(id);

        assert (deletedVideo == null);
    }
}
