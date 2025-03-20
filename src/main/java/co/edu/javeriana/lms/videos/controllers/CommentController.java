package co.edu.javeriana.lms.videos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.shared.dtos.ApiResponseDto;
import co.edu.javeriana.lms.videos.dtos.CommentDto;
import co.edu.javeriana.lms.videos.models.Comment;
import co.edu.javeriana.lms.videos.services.CommentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@RestController
@RequestMapping(value = "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<List<Comment>>> getCommentsByVideo(@PathVariable Long id) {
        log.info("Requesting comments for video with id: {}", id);

        List<Comment> comments = commentService.getCommentsByVideo(id);

        if (comments == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Video not found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<List<Comment>>(HttpStatus.OK.value(), "ok", comments, null));
    }
    

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Comment>> addCommentToVideo(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        log.info("Requesting video with id: {}", id);

        Comment comment = commentService.addCommentToVideo(id, commentDto);

        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(HttpStatus.NOT_FOUND.value(), "Video not found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDto<Comment>(HttpStatus.OK.value(), "ok", comment, null));
    }
}
