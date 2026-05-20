package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.notification.NotificationRequest;
import vi.wbca.webcinema.model.entity.notification.Notification;
import vi.wbca.webcinema.model.response.notification.NotificationResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "read", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "readTime", ignore = true)
    @Mapping(target = "user", ignore = true)
    Notification toNotification(NotificationRequest request);

    NotificationResponse toNotificationResponse(Notification notification);
}