package co.edu.javeriana.lms.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.videos.dtos.ArecVideosResponseDto;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import co.edu.javeriana.lms.videos.services.ArecService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ArecServiceTest {

    @InjectMocks
    private ArecService arecService;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private SimulationRepository simulationRepository;

    @Test
    public void testAssociateVideoWithSimulation() throws URISyntaxException, IOException, InterruptedException {
        Long roomId = 1L;
        String videoName = "videoName";
        Long simulationId = 2L;
        Video video = Video.builder()
                .name("Test Video")
                .recordingDate(new Date())
                .duration(10L)
                .size(100.0)
                .videoUrl("playbackUrl")
                .available(true)
                .comments(List.of())
                .build();

        ArecVideosResponseDto.Video arecVideo = new ArecVideosResponseDto.Video("Test Video", 10.0, new Date(), new Date(),
                "finished", List.of(new ArecVideosResponseDto.VideoMetadata("Movie", "playbackUrl",
                        "downloadUrl", 10.0, "thumbnail")));

        when(videoRepository.findByName(videoName)).thenReturn(Optional.empty());
        when(simulationRepository.findAllByRooms_IdAndStartDateTimeAfterAndEndDateTimeBefore(eq(roomId), any(), any()))
                .thenReturn(List.of(Simulation.builder()
                        .simulationId(simulationId)
                        .build()));
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        arecService.associateVideoWithSimulation(roomId, arecVideo);
    }
}
