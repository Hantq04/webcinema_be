package vi.wbca.webcinema.model.dto.setting;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneralSettingDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @Schema(description = "Break time used to block creating schedules at this hour", example = "12:00:00")
    @NotNull(message = "NOT_BLANK")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime breakTime;

    @Schema(description = "Number of operating hours from open time to close time", example = "14")
    @NotNull(message = "NOT_BLANK")
    @Min(value = 1, message = "VALIDATE_ERROR")
    Integer businessHours;

    @Schema(description = "Opening time of the cinema", example = "08:00:00")
    @NotNull(message = "NOT_BLANK")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime openTime;

    @Schema(description = "Computed closing time based on openTime + businessHours", example = "22:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime closeTime;

    @Schema(description = "Weekend surcharge percentage applied to ticket prices", example = "15")
    @NotNull(message = "NOT_BLANK")
    @Min(value = 0, message = "VALIDATE_ERROR")
    @Max(value = 100, message = "VALIDATE_ERROR")
    Integer percentWeekend;

    @Schema(description = "Only schedules created after this timestamp are validated by the new open/close rules", example = "2026-05-01 00:00:00")
    @NotNull(message = "NOT_BLANK")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime timeBeginToChange;
}
