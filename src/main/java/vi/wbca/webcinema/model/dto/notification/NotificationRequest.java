package vi.wbca.webcinema.model.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.enums.NotificationTypeEnum;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
    @Schema(description = "Notification title", example = "Movie status updated")
    @NotBlank(message = "NOT_BLANK")
    String title;

    @Schema(description = "Notification content", example = "Mortal Kombat II has been updated.")
    @NotBlank(message = "NOT_BLANK")
    String content;

    @Schema(description = "Notification type", example = "JOB")
    @NotNull(message = "NOT_BLANK")
    NotificationTypeEnum type;

    @Schema(description = "Target usernames. If empty and broadcastToAdminStaff is true, system will send to all admin/staff users.")
    List<String> userNames;

    @Schema(description = "Broadcast to all active admin/staff users", example = "true")
    boolean broadcastToAdminStaff;
}