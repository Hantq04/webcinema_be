package vi.wbca.webcinema.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vi.wbca.webcinema.service.impl.TicketHoldCleanupService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketHoldScheduler {
    private final TicketHoldCleanupService ticketHoldCleanupService;

    @Scheduled(fixedDelay = 30000)
    public void cleanupExpiredTicketHolds() {
        try {
            ticketHoldCleanupService.cleanupExpiredTicketHolds();
        } catch (Exception e) {
            log.error("An error occurred during the ticket hold cleanup process:", e);
        }
    }
}