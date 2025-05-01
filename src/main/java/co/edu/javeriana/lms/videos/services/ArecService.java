package co.edu.javeriana.lms.videos.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import co.edu.javeriana.lms.videos.dtos.ArecLoginRequestDto;
import co.edu.javeriana.lms.videos.dtos.ArecLoginResponseDto;
import co.edu.javeriana.lms.videos.dtos.ArecVideosResponseDto;
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

    private ArecLoginResponseDto loginToArec(String ipAddress)
            throws URISyntaxException, IOException, InterruptedException {
        log.info("Logging in to Arec with username: {}", AREC_USERNAME);
        Gson gson = new Gson();

        String base64Auth = Base64.getEncoder()
                .encodeToString(String.format("%s:%s", AREC_USERNAME, AREC_PASSWORD).getBytes(StandardCharsets.UTF_8));

        String req = gson.toJson(new ArecLoginRequestDto(AREC_USERNAME, base64Auth, "javeriana", "0"));
        log.info("Request to Arec: {}", req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + ipAddress + AREC_LOGIN_PATH))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<String> setCookieHeaders = response.headers().allValues("Set-Cookie");

        ArecLoginResponseDto cookies = new ArecLoginResponseDto();

        if (!setCookieHeaders.isEmpty()) {
            log.info("Set-Cookie headers received:");
            for (String setCookieHeader : setCookieHeaders) {
                if (setCookieHeader.startsWith("session=")) {
                    cookies.setSession(setCookieHeader.split(";")[0].split("=")[1]);
                    log.info("Session cookie: {}", cookies.getSession());
                } else if (setCookieHeader.startsWith("path=")) {
                    cookies.setPath(setCookieHeader.split(";")[0].split("=")[1]);
                    log.info("Path cookie: {}", cookies.getPath());
                } else {
                    log.warn("Unexpected Set-Cookie header: {}", setCookieHeader);
                }
            }
        } else {
            log.error("No Set-Cookie header received in the response.");
        }

        return cookies;
    }

    private ArecVideosResponseDto fetchVideos(String ipAddress)
            throws URISyntaxException, IOException, InterruptedException {
        log.info("Getting videos from Arec");
        Gson gson = new Gson();

        ArecLoginResponseDto cookies = loginToArec(ipAddress);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + ipAddress + AREC_RECORDINGS_PATH))
                .header("Content-Type", "application/json")
                .header("Cookie", "session=" + cookies.getSession() + "; " + cookies.getPath() + "; HttpOnly")
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
                    .header("Cookie", "session=" + cookies.getSession() + "; " + cookies.getPath() + "; HttpOnly")
                    .GET()
                    .build();
        }
        return res;
    }

    private void associateVideoWithSimulation(ArecVideosResponseDto.Video video)
            throws URISyntaxException, IOException, InterruptedException {
        
    }

    public void syncVideos(String ipAddress) throws URISyntaxException, IOException, InterruptedException {
        log.info("Syncing videos with Arec");

        ArecVideosResponseDto res = fetchVideos(ipAddress);

        for (ArecVideosResponseDto.Video video : res.getResults()) {
            log.info("Video name: {}", video.getName());
            log.info("Video length: {}", video.getLength());
            log.info("Video recorded at: {}", video.getRecordedAt());
            log.info("Video finished at: {}", video.getFinishedAt());

            associateVideoWithSimulation(video);
        }
    }
}
