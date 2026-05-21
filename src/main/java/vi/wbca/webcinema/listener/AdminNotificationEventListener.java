package vi.wbca.webcinema.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import vi.wbca.webcinema.enums.RoleEnum;
import vi.wbca.webcinema.event.AdminNotificationEvent;
import vi.wbca.webcinema.model.entity.notification.Notification;
import vi.wbca.webcinema.repository.notification.NotificationRepo;
import vi.wbca.webcinema.repository.user.UserRepo;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminNotificationEventListener {
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdminNotification(AdminNotificationEvent event) {
        userRepo.findAllByRoleAndIsActiveTrue(RoleEnum.ADMIN).forEach(admin -> {
            Notification notification = new Notification();
            notification.setTitle(event.getTitle());
            notification.setContent(event.getContent());
            notification.setType(event.getType());
            notification.setUser(admin);
            notification.setRead(false);
            notification.setCreateTime(LocalDateTime.now());
            notificationRepo.save(notification);
        });
    }
}