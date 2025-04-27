package co.edu.javeriana.lms.videos.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import co.edu.javeriana.lms.shared.dtos.ArecLoginRequestDto;
import co.edu.javeriana.lms.shared.dtos.ArecLoginResponseDto;
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
    private String accessToken;

    private ArecLoginResponseDto loginToArec() throws URISyntaxException, IOException, InterruptedException {
        log.info("Logging in to Arec with username: {}", AREC_USERNAME);
        Gson gson = new Gson();

        String base64Auth = Base64.getEncoder().encodeToString(String.format("%s:%s", AREC_USERNAME, AREC_PASSWORD).getBytes(StandardCharsets.UTF_8));
        
        String req = gson.toJson(new ArecLoginRequestDto(AREC_USERNAME, base64Auth, "javeriana", "0"));
        log.info("Request to Arec: {}", req);

        // HttpRequest request = HttpRequest.newBuilder()
        //         .uri(new URI("http://localhost:8081" + AREC_LOGIN_PATH))
        //         .header("Content-Type", "application/json")
        //         .POST(HttpRequest.BodyPublishers.ofString(req))
        //         .build();

        // HttpClient client = HttpClient.newHttpClient();
        // HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // ArecLoginResponseDto res = gson.fromJson(response.body(), ArecLoginResponseDto.class);
        ArecLoginResponseDto res = new ArecLoginResponseDto("accessToken", "refreshToken");

        accessToken = res.getAccessToken();

        return res;
    }

    public void syncVideos() throws URISyntaxException, IOException, InterruptedException {
        log.info("Syncing videos with Arec");
        loginToArec();
    }
}
