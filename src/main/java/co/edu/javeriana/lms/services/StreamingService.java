package co.edu.javeriana.lms.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StreamingService {
    private static final String FORMAT = "src/main/resources/videos/%s.mp4";

    public ResponseEntity<Resource> getVideo(String title, String rangeHeader) throws IOException {
        Path videoPath = Paths.get(String.format(FORMAT, title));

        if (!Files.exists(videoPath)) {
            log.error("Video not found: {}", title);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        long fileSize = Files.size(videoPath);
        byte[] videoBytes;
        long start = 0;
        long end = fileSize - 1;

        // Handle Range Header
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        if (end >= fileSize) {
            end = fileSize - 1;
        }

        long contentLength = end - start + 1;

        try (RandomAccessFile file = new RandomAccessFile(videoPath.toFile(), "r")) {
            file.seek(start);
            videoBytes = new byte[(int) contentLength];
            file.readFully(videoBytes);
        }

        InputStreamResource resource = new InputStreamResource(new InputStream() {
            private int index = 0;

            @Override
            public int read() throws IOException {
                return (index < videoBytes.length) ? (videoBytes[index++] & 0xFF) : -1;
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "video/mp4");
        headers.add("Content-Length", String.valueOf(contentLength));
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(resource);
    }
}
