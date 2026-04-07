package vi.wbca.webcinema.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vi.wbca.webcinema.repository.movie.MovieRepo;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieScheduler {

    private final MovieRepo movieRepo;

    /**
     * Automatically updates isActive = false for expired movies.
     * Runs at 00:00 every day.
     * Cron expression: "0 0 0 * * *" (second minute hour day month weekday)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void autoUpdateMovieStatus() {
        log.info("Starting automated process to update expired movie status...");
        try {
            LocalDateTime now = LocalDateTime.now();
            movieRepo.updateExpiredMovies(now);
            log.info("Movie status update completed at: {}", now);
        } catch (Exception e) {
            log.error("An error occurred during the movie status update process: ", e);
        }
    }
}
