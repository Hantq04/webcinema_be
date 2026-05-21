package vi.wbca.webcinema.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vi.wbca.webcinema.enums.NotificationTypeEnum;

@Getter
@AllArgsConstructor
public class AdminNotificationEvent {
    private final NotificationTypeEnum type;
    private final String title;
    private final String content;
}