package vi.wbca.webcinema.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vi.wbca.webcinema.model.dto.notification.NotificationRequest;
import vi.wbca.webcinema.model.response.notification.NotificationResponse;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);

    Page<NotificationResponse> getMyNotifications(Pageable pageable);

    long getUnreadCount();

    NotificationResponse markAsRead(Long id);

    void markAllAsRead();
}