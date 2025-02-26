package co.edu.javeriana.lms.videos.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.videos.services.StreamingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/streaming")
public class StreamingController {

    @Autowired
    private StreamingService service;

    @GetMapping(value = "/video/{title}", produces = "video/mp4")
    public ResponseEntity<Resource> getVideo(@Valid @PathVariable String title,
            @Valid @RequestHeader(value = "Range", required = false) String range) throws IOException {
        log.info("Request received for video: {}, requesting Range of: {}", title, range);
        return service.getVideo(title, range);
    }
}
