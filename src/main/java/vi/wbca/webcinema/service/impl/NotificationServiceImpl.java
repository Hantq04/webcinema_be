package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.enums.RoleEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.NotificationMapper;
import vi.wbca.webcinema.model.dto.notification.NotificationRequest;
import vi.wbca.webcinema.model.entity.notification.Notification;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.response.notification.NotificationResponse;
import vi.wbca.webcinema.repository.notification.NotificationRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotificationMapper notificationMapper;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        List<User> targetUsers = resolveTargetUsers(request);
        if (targetUsers.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Notification savedNotification = null;
        for (User user : targetUsers) {
            Notification notification = notificationMapper.toNotification(request);
            notification.setUser(user);
            notification.setRead(false);
            notification.setCreateTime(LocalDateTime.now());
            savedNotification = notificationRepo.save(notification);
        }

        return notificationMapper.toNotificationResponse(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        User currentUser = getCurrentUser();
        return notificationRepo.findAllByUserOrderByCreateTimeDesc(currentUser, pageable)
                .map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        return notificationRepo.countByUserAndIsReadFalse(currentUser);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        User currentUser = getCurrentUser();
        Notification notification = notificationRepo.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        notification.setRead(true);
        notification.setReadTime(LocalDateTime.now());
        return notificationMapper.toNotificationResponse(notificationRepo.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        List<Notification> notifications = notificationRepo.findAllByUserOrderByCreateTimeDesc(currentUser, Pageable.unpaged()).getContent();
        LocalDateTime now = LocalDateTime.now();
        notifications.forEach(notification -> {
            if (!notification.isRead()) {
                notification.setRead(true);
                notification.setReadTime(now);
            }
        });
        notificationRepo.saveAll(notifications);
    }

    private List<User> resolveTargetUsers(NotificationRequest request) {
        if (request.isBroadcastToAdminStaff()) {
            return userRepo.findAll().stream()
                    .filter(User::isActive)
                    .filter(user -> user.getRole() == RoleEnum.ADMIN || user.getRole() == RoleEnum.STAFF)
                    .toList();
        }

        if (request.getUserNames() != null && !request.getUserNames().isEmpty()) {
            return request.getUserNames().stream()
                    .map(userName -> userRepo.findByUserName(userName)
                            .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND)))
                    .filter(User::isActive)
                    .toList();
        }

        return List.of(getCurrentUser());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepo.findByUserName(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }
}