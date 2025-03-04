package co.edu.javeriana.lms.videos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.videos.dtos.EditVideoDto;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public Page<Video> searchVideos(String filter, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return videoRepository.findByNameContainingIgnoreCase(filter, pageable);
    }

    public Video editVideo(Long id, EditVideoDto video) {
        Video videoToEdit = videoRepository.findById(id).orElse(null);
        if (videoToEdit == null) {
            return null;
        }

        videoToEdit.setName(video.getName());
        videoToEdit.setExpirationDate(video.getExpirationDate());
    
        return videoRepository.save(videoToEdit);
    }

    public Video deleteVideo(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return null;
        }

        videoRepository.delete(video);
        return video;
    }

}
