package co.edu.javeriana.lms.config.handlers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.booking.models.Room;
import co.edu.javeriana.lms.booking.repositories.RoomRepository;
import co.edu.javeriana.lms.videos.services.ArecService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduleTaskHandler {

    @Autowired
    private ArecService arecService;

    @Autowired
    private RoomRepository roomRepository;

    @Value("${AREC_SYNC}")
    private Boolean arecSync;

    // @Scheduled(cron = "0 0 6 * * *") // Cron expression for running 6AM every day
    @Scheduled(cron = "0 * * * * *") // Cron expression for running every minute
    public void syncArecVideos() throws URISyntaxException, IOException, InterruptedException {
        log.info("Scheduled task to sync videos with Arec started");
        List<Room> rooms = roomRepository.findAll();

        if (!arecSync) {
            log.info("Arec sync is disabled. Skipping video sync.");
            return;
        }
        rooms.forEach(room -> {
            try {
                log.info("Syncing videos for room: {}", room.getName());
                arecService.syncVideos(room.getId(), room.getIp());
            } catch (Exception e) {
                log.error("Error syncing videos for room {}: {}", room.getName(), e.getMessage());
            }
        });
    }
}
