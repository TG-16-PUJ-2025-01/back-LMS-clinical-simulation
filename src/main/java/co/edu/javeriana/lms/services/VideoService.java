package co.edu.javeriana.lms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.models.Video;
import co.edu.javeriana.lms.repositories.VideoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public Page<Video> getAllVideos(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return videoRepository.findAll(pageable);
    }

}
