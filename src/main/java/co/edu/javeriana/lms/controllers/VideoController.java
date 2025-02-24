package co.edu.javeriana.lms.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.dtos.ApiResponseDto;
import co.edu.javeriana.lms.dtos.EditVideoDto;
import co.edu.javeriana.lms.dtos.PaginationMetadataDto;
import co.edu.javeriana.lms.models.Video;
import co.edu.javeriana.lms.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@Slf4j
@RestController
@RequestMapping(value = "/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto<?>> getAllVideos(
            @Min(0) @RequestParam(defaultValue = "0") Integer page,
            @Min(1) @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "videoId") String sort,
            @RequestParam(defaultValue = "true") Boolean asc,
            @RequestParam(defaultValue = "") String filter,
            HttpServletRequest request) {
        log.info("Requesting all simulations");

        String host = request.getHeader("Host");
        String scheme = request.getScheme();

        Page<Video> videosPage = videoService.searchVideos(filter, page, size, sort, asc);

        String previous = null;
        if (videosPage.hasPrevious()) {
            previous = String.format("%s://%s/simulation/all?page=%d&size=%d", scheme, host, page - 1, size);
        }

        String next = null;
        if (videosPage.hasNext()) {
            next = String.format("%s://%s/simulation/all?page=%d&size=%d", scheme, host, page + 1, size);
        }

        PaginationMetadataDto metadata = new PaginationMetadataDto(page, videosPage.getNumberOfElements(),
                videosPage.getTotalElements(), videosPage.getTotalPages(), next,
                previous);

        return ResponseEntity.ok(
                new ApiResponseDto<List<Video>>(HttpStatus.OK.value(), "ok", videosPage.getContent(), metadata));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> editVideo(@PathVariable Long id, @Valid @RequestBody EditVideoDto dto) {
        log.info("Editing video with id: {}", id);

        Video video = videoService.editVideo(id, dto);

        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Video not found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<Video>(HttpStatus.OK.value(), "ok", video, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> deleteVideo(@PathVariable Long id) {
        log.info("Deleting video with id: {}", id);

        Video video = videoService.deleteVideo(id);

        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Video not found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<Video>(HttpStatus.OK.value(), "ok", video, null));
    }

}
