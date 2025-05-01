package co.edu.javeriana.lms.config.handlers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import co.edu.javeriana.lms.videos.services.ArecService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduleTaskHandler {

    @Autowired
    private ArecService arecService;

    // @Scheduled(cron = "0 0 6 * * *") // Cron expression for running 6AM every day
    @Scheduled(cron = "0 * * * * *") // Cron expression for running every minute
    public void syncArecVideos() throws URISyntaxException, IOException, InterruptedException {
        log.info("Scheduled task to sync videos with Arec started");
        // arecService.syncVideos("");
    }
}
