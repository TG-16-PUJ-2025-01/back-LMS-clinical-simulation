package co.edu.javeriana.lms.videos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.videos.dtos.CommentDto;
import co.edu.javeriana.lms.videos.models.Comment;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private VideoRepository videoRepository;

    public List<Comment> getCommentsByVideo(Long videoId) {
        log.info("Getting comments for video with id: {}", videoId);

        Video video = videoRepository.findById(videoId).orElseThrow(() -> new EntityNotFoundException("Video not found"));
        return video.getComments();
    }

    public Comment addCommentToVideo(Long videoId, CommentDto commentDto) {
        log.info("Adding comment to video with id: {}", videoId);

        Video video = videoRepository.findById(videoId).orElseThrow(() -> new EntityNotFoundException("Video not found"));
        video.getComments().add(commentDto.toEntity());
        videoRepository.save(video);
        return commentDto.toEntity();
    }
}
