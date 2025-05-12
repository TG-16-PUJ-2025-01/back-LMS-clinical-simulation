package co.edu.javeriana.lms.videos.services;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.videos.dtos.ArecLoginRequestDto;
import co.edu.javeriana.lms.videos.dtos.ArecLoginResponseDto;
import co.edu.javeriana.lms.videos.dtos.ArecVideosResponseDto;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ArecService {

    @Value("${AREC_USERNAME}")
    private String AREC_USERNAME;

    @Value("${AREC_PASSWORD}")
    private String AREC_PASSWORD;

    private static final String AREC_LOGIN_PATH = "/api/login";
    private static final String AREC_RECORDINGS_PATH = "/api/recording";

    private Gson gson;

    private final WebClient webClient;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    public ArecService(WebClient webClient) {
        this.webClient = webClient;
        gson = new Gson();
    }

    public String encodeCredentials(String username, String password) {
        return Base64.getEncoder()
                .encodeToString(String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8));
    }

    public ArecLoginResponseDto loginToArec(String ipAddress)
            throws URISyntaxException, IOException, InterruptedException {
        log.info("Logging in to Arec with username: {}", AREC_USERNAME);
        String base64Auth = encodeCredentials(AREC_USERNAME, AREC_PASSWORD);
        ArecLoginRequestDto body = new ArecLoginRequestDto(AREC_USERNAME, base64Auth, "javeriana", "0");
        log.info("Request body JSON: {}", gson.toJson(body));

        return webClient.post()
                .uri("http://" + ipAddress + AREC_LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response -> {
                    String setCookie = response.headers().asHttpHeaders().getFirst(HttpHeaders.SET_COOKIE);
                    ArecLoginResponseDto dto = new ArecLoginResponseDto();

                    if (setCookie != null) {
                        List<HttpCookie> cookies = HttpCookie.parse(setCookie);
                        for (HttpCookie cookie : cookies) {
                            if ("session".equals(cookie.getName())) {
                                dto.setSession(cookie.getValue());
                            }
                        }
                    }

                    return Mono.just(dto);
                })
                .block();
    }

    public ArecVideosResponseDto fetchVideos(String ipAddress)
            throws URISyntaxException, IOException, InterruptedException {
        log.info("Getting videos from Arec");

        ArecLoginResponseDto cookies = loginToArec(ipAddress);
        log.info("Session cookie: {}", cookies.getSession());

        ArecVideosResponseDto res = webClient.get()
                .uri("http://" + ipAddress + AREC_RECORDINGS_PATH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.COOKIE, "session=" + cookies.getSession())
                .retrieve()
                .bodyToMono(ArecVideosResponseDto.class)
                .block();

        log.info("Response from Arec: {}", gson.toJson(res));
        log.info("Total videos: {}", res.getPageInfo().getTotal());

        if (res.getPageInfo().getTotal() != res.getPageInfo().getCount()) {
            res = webClient.get()
                    .uri("http://" + ipAddress + AREC_RECORDINGS_PATH + "?per_page=" + res.getPageInfo().getTotal())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.COOKIE, "session=" + cookies.getSession())
                    .retrieve()
                    .bodyToMono(ArecVideosResponseDto.class)
                    .block();
        }
        return res;
    }

    public void associateVideoWithSimulation(Long roomId, ArecVideosResponseDto.Video video)
            throws URISyntaxException, IOException, InterruptedException {
        log.info("Associating video with simulation " + video.getName());

        Optional<Video> videoData = videoRepository.findByName(video.getName());

        if (videoData.isPresent()) {
            log.info("Video already exists in the database");
            return;
        }
        ArecVideosResponseDto.VideoMetadata videoMetadata = video.getMetadata().stream()
                .filter(metadata -> metadata.getChannelName().equals("Movie"))
                .findFirst()
                .orElse(null);

        if (videoMetadata == null) {
            log.error("No metadata found with channelName 'movie' for video: {}", video.getName());
            return;
        }

        Video newVideo = Video.builder()
                .name(video.getName())
                .duration(video.getLength().longValue())
                .recordingDate(video.getRecordedAt())
                .available(video.getStatus().equals("ready"))
                .videoUrl(videoMetadata.getPlaybackUrl())
                .size(videoMetadata.getSize() / 1000000)
                .build();

        List<Simulation> simulations = simulationRepository.findAllByRooms_IdAndStartDateTimeAfterAndEndDateTimeBefore(
                roomId, video.getRecordedAt(), video.getFinishedAt());

        if (simulations.isEmpty()) {
            log.error("No simulation found for video: {}", video.getName());
            return;
        }
        if (simulations.size() > 1) {
            log.error("Multiple simulations found for video: {}", video.getName());
            return;
        }
        Simulation simulation = simulations.get(0);
        log.info("Simulation found: {}", simulation.getSimulationId());
        newVideo.setSimulation(simulation);
        videoRepository.save(newVideo);
        log.info("Video saved: {}", newVideo.getName());
    }

    public void syncVideos(Long roomId, String ipAddress) throws URISyntaxException, IOException, InterruptedException {
        log.info("Syncing videos with Arec");

        ArecVideosResponseDto res = fetchVideos(ipAddress);

        for (ArecVideosResponseDto.Video video : res.getResult()) {
            log.info("Video name: {}", video.getName());
            log.info("Video length: {}", video.getLength());
            log.info("Video recorded at: {}", video.getRecordedAt());
            log.info("Video finished at: {}", video.getFinishedAt());

            associateVideoWithSimulation(roomId, video);
        }
    }
}
