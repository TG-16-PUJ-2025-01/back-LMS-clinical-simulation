package co.edu.javeriana.lms.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import co.edu.javeriana.lms.videos.dtos.ArecLoginRequestDto;
import co.edu.javeriana.lms.videos.dtos.ArecLoginResponseDto;
import co.edu.javeriana.lms.videos.dtos.ArecVideosResponseDto;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import co.edu.javeriana.lms.videos.services.ArecService;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ArecServiceTest {

    private ArecService arecService;

    @Mock
    WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec2;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec2;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private SimulationRepository simulationRepository;

    private static final String mockIp = "mockIp";
    private Gson gson;

    @BeforeEach
    public void setUp() {
        gson = new Gson();
        arecService = new ArecService(webClient);
    }

    @Test
    public void testLogin() throws URISyntaxException, IOException, InterruptedException {
        ArecLoginResponseDto login = new ArecLoginResponseDto("abc123");

        when(webClient.post())
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://mockIp/api/login"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(ArecLoginRequestDto.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any()))
                .thenReturn(Mono.just(login));

        ArecLoginResponseDto response = arecService.loginToArec(mockIp);

        assert response != null;
        assert response.getSession().equals(login.getSession());
    }

    @Test
    public void testFetchVideos() throws URISyntaxException, IOException, InterruptedException {
        ArecLoginResponseDto login = new ArecLoginResponseDto("abc123");
        
        when(webClient.post())
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://mockIp/api/login"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(ArecLoginRequestDto.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any()))
                .thenReturn(Mono.just(login));

        String jsonResponse = new String(Files.readAllBytes(Paths.get("./src/test/resources/arecVideosResponse.json")));
        ArecVideosResponseDto response = gson.fromJson(jsonResponse, ArecVideosResponseDto.class);

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://mockIp/api/recording"))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(HttpHeaders.COOKIE, "session=" + login.getSession()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ArecVideosResponseDto.class))
                .thenReturn(Mono.just(response));

        ArecVideosResponseDto result = arecService.fetchVideos(mockIp);

        assert result != null;
        assert result.equals(response);
    }

    @Test
    public void testFetchVideosDifferentCount() throws URISyntaxException, IOException, InterruptedException {
        ArecLoginResponseDto login = new ArecLoginResponseDto("abc123");
        
        when(webClient.post())
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://mockIp/api/login"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(ArecLoginRequestDto.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any()))
                .thenReturn(Mono.just(login));

        String jsonResponse = new String(Files.readAllBytes(Paths.get("./src/test/resources/arecVideosResponse.json")));
        ArecVideosResponseDto response = gson.fromJson(jsonResponse, ArecVideosResponseDto.class);
        String jsonResponse2 = new String(Files.readAllBytes(Paths.get("./src/test/resources/arecVideosResponse2.json")));
        ArecVideosResponseDto response2 = gson.fromJson(jsonResponse2, ArecVideosResponseDto.class);

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://mockIp/api/recording"))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(HttpHeaders.COOKIE, "session=" + login.getSession()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ArecVideosResponseDto.class))
                .thenReturn(Mono.just(response));

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://mockIp/api/recording?per_page=" + response2.getPageInfo().getTotal()))
                .thenReturn(requestHeadersSpec2);
        when(requestHeadersSpec2.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestHeadersSpec2);
        when(requestHeadersSpec2.header(HttpHeaders.COOKIE, "session=" + login.getSession()))
                .thenReturn(requestHeadersSpec2);
        when(requestHeadersSpec2.retrieve())
                .thenReturn(responseSpec2);
        when(responseSpec2.bodyToMono(ArecVideosResponseDto.class))
                .thenReturn(Mono.just(response2));

        ArecVideosResponseDto result = arecService.fetchVideos(mockIp);

        assert result != null;
        assert result.equals(response);
    }
}
