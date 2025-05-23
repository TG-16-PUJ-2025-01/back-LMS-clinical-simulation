package co.edu.javeriana.lms.videos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import co.edu.javeriana.lms.practices.models.Simulation;
import co.edu.javeriana.lms.videos.dtos.EditVideoDto;
import co.edu.javeriana.lms.videos.models.Video;
import co.edu.javeriana.lms.videos.repositories.VideoRepository;
import co.edu.javeriana.lms.practices.repositories.SimulationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private SimulationRepository simulationRepository;

    public Page<Video> searchVideos(String filter, Integer page, Integer size, String sort, Boolean asc) {
        Sort sortOrder = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return videoRepository.findByNameContainingIgnoreCase(filter, pageable);
    }

    public Video getVideo(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    public Video editVideo(Long id, EditVideoDto video) {
        Video videoToEdit = videoRepository.findById(id).orElse(null);
        if (videoToEdit == null) {
            return null;
        }

        videoToEdit.setName(video.getName());

        // Only modify the simulation if there is a change
        Long currentSimulationId = videoToEdit.getSimulation() != null ? videoToEdit.getSimulation().getSimulationId() : null;
        Long newSimulationId = video.getSimulationId();
        if ((currentSimulationId != null && !currentSimulationId.equals(newSimulationId)) || (currentSimulationId == null && newSimulationId != null)) {
            // If the new simulation is not null, we need to check if it exists
            if (newSimulationId != null) {
                Simulation newSimulation = simulationRepository.findById(newSimulationId).orElse(null);
                if (newSimulation != null) {
                    // Remove the video from the old simulation
                    Simulation oldSimulation = videoToEdit.getSimulation();
                    if (oldSimulation != null && oldSimulation.getVideos() != null) {
                        oldSimulation.getVideos().remove(videoToEdit);
                    }
                    videoToEdit.setSimulation(newSimulation);
                    if (newSimulation.getVideos() != null && !newSimulation.getVideos().contains(videoToEdit)) {
                        newSimulation.getVideos().add(videoToEdit);
                    }
                }
            } else {
                // If the new simulation is null, we need to remove the video from the current simulation
                Simulation oldSimulation = videoToEdit.getSimulation();
                if (oldSimulation != null && oldSimulation.getVideos() != null) {
                    oldSimulation.getVideos().remove(videoToEdit);
                }
                videoToEdit.setSimulation(null);
            }
        }

        return videoRepository.save(videoToEdit);
    }

    public Video setVideoAsUnavailable(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return null;
        }

        video.setAvailable(false);
        return videoRepository.save(video);
    }

    public Video deleteVideo(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return null;
        }

        if (video.getSimulation() != null) {
            Simulation simulation = video.getSimulation();
            simulation.getVideos().remove(video);
        }

        videoRepository.delete(video);
        return video;
    }

    public Simulation getVideoSimulation(Long id) {
        Video video = videoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Video not found with id: " + id));

        return video.getSimulation();
    }

}
