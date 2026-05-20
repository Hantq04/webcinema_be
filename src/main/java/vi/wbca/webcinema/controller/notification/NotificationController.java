package vi.wbca.webcinema.controller.notification;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.notification.NotificationRequest;
import vi.wbca.webcinema.model.response.notification.NotificationResponse;
import vi.wbca.webcinema.service.NotificationService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private static final Logger logger = Logger.getLogger(NotificationController.class.getName());
    private final NotificationService notificationService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Tạo thông báo mới")
    public ResponseEntity<ResponseObject> createNotification(@Valid @RequestBody NotificationRequest request) {
        logger.info("----------Web Cinema: Create Notification----------");
        NotificationResponse responseData = notificationService.createNotification(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_notification", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject(HttpStatus.CREATED, message, responseData)
        );
    }

    @GetMapping("/get-all-notification")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lấy danh sách tất cả thông báo")
    public ResponseEntity<ResponseObject> getAllNotification(@RequestParam int page, @RequestParam int size) {
        logger.info("----------Web Cinema: Get All Notification----------");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<NotificationResponse> responseData = notificationService.getMyNotifications(pageable);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_notification", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/unread-count")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Hiển thị số lượng thông báo chưa đọc")
    public ResponseEntity<ResponseObject> getUnreadCount() {
        logger.info("----------Web Cinema: Get Unread Notification Count----------");
        long unreadCount = notificationService.getUnreadCount();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_unread_notification_count", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, unreadCount)
        );
    }

    @PutMapping("/mark-read")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Đánh dấu một thông báo là đã đọc")
    public ResponseEntity<ResponseObject> markAsRead(@RequestParam Long id) {
        logger.info("----------Web Cinema: Mark Notification As Read----------");
        NotificationResponse responseData = notificationService.markAsRead(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.mark_notification_read", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/mark-all-read")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Đánh dấu tất cả thông báo là đã đọc")
    public ResponseEntity<ResponseObject> markAllAsRead() {
        logger.info("----------Web Cinema: Mark All Notification As Read----------");
        notificationService.markAllAsRead();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.mark_all_notification_read", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }
}