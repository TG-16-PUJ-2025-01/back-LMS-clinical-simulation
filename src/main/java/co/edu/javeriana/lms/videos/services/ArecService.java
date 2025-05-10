package co.edu.javeriana.lms.videos.services;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.videos.dtos.ArecLoginRequestDto;
import co.edu.javeriana.lms.videos.dtos.ArecLoginResponseDto;
import co.edu.javeriana.lms.videos.dtos.ArecVideosResponseDto;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ArecService {
    @Value("${AREC_USERNAME}")
    private String AREC_USERNAME;
    @Value("${AREC_PASSWORD}")
    private String AREC_PASSWORD;
    private static final String AREC_LOGIN_PATH = "/api/login";
    private static final String AREC_RECORDINGS_PATH = "/api/recording";

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    public String encodeCredentials(String username, String password) {
        return Base64.getEncoder()
                .encodeToString(String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8));
    }

    public ArecLoginResponseDto loginToArec(String ipAddress)
            throws URISyntaxException, IOException, InterruptedException {
        log.info("Logging in to Arec with username: {}", AREC_USERNAME);
        Gson gson = new Gson();

        String base64Auth = encodeCredentials(AREC_USERNAME, AREC_PASSWORD);

        String req = gson.toJson(new ArecLoginRequestDto(AREC_USERNAME, base64Auth, "javeriana", "0"));
        log.info("Request to Arec: {}", req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + ipAddress + AREC_LOGIN_PATH))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Response from Arec: {}", response.body());
        log.info("Response status code: {}", response.statusCode());
        log.info("Response headers: {}", response.headers().map());

        String setCookieHeader = response.headers().firstValue("Set-Cookie")
                .orElseThrow(() -> new RuntimeException("No Set-Cookie header found"));
        log.info("Set-Cookie headers: {}", setCookieHeader);

        ArecLoginResponseDto cookies = new ArecLoginResponseDto();

        log.info("Set-Cookie: {}", setCookieHeader);

        List<HttpCookie> setCookies = HttpCookie.parse(setCookieHeader);
        for (HttpCookie setCookie : setCookies) {
            log.info("Name: " + setCookie.getName());
            log.info("Value: " + setCookie.getValue());
            log.info("Domain: " + setCookie.getDomain());
            log.info("Path: " + setCookie.getPath());
            log.info("Max Age: " + setCookie.getMaxAge());
            log.info("Secure: " + setCookie.getSecure());
            log.info("HttpOnly: " + setCookie.isHttpOnly());
            log.info("Version: " + setCookie.getVersion());
            log.info("Comment: " + setCookie.getComment());
            if (setCookie.getName().equals("session")) {
                cookies.setSession(setCookie.getValue());
            }
        }

        return cookies;
    }

    public ArecVideosResponseDto fetchVideos(String ipAddress)
            throws URISyntaxException, IOException, InterruptedException {
        log.info("Getting videos from Arec");
        Gson gson = new Gson();

        ArecLoginResponseDto cookies = loginToArec(ipAddress);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + ipAddress + AREC_RECORDINGS_PATH))
                .header("Content-Type", "application/json")
                .header("Cookie", "session=" + cookies.getSession())
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ArecVideosResponseDto res = gson.fromJson(response.body(), ArecVideosResponseDto.class);

        log.info("Response from Arec: {}", response.body());
        log.info("Total videos: {}", res.getPageInfo().getTotal());

        if (res.getPageInfo().getTotal() != res.getPageInfo().getCount()) {
            request = HttpRequest.newBuilder()
                    .uri(new URI(
                            "http://" + ipAddress + AREC_RECORDINGS_PATH + "?per_page=" + res.getPageInfo().getTotal()))
                    .header("Content-Type", "application/json")
                    .header("Cookie", "session=" + cookies.getSession())
                    .GET()
                    .build();
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

        for (ArecVideosResponseDto.Video video : res.getResults()) {
            log.info("Video name: {}", video.getName());
            log.info("Video length: {}", video.getLength());
            log.info("Video recorded at: {}", video.getRecordedAt());
            log.info("Video finished at: {}", video.getFinishedAt());

            associateVideoWithSimulation(roomId, video);
        }
    }
}
