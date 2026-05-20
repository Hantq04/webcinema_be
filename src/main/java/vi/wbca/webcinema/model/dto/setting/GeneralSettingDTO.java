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

    @NotNull(message = "NOT_BLANK")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime breakTime;

    @NotNull(message = "NOT_BLANK")
    @Min(value = 1, message = "VALIDATE_ERROR")
    Integer businessHours;

    @Schema(description = "Opening time of the cinema", example = "08:00:00")
    @NotNull(message = "NOT_BLANK")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime openTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime closeTime;

    @NotNull(message = "NOT_BLANK")
    @Min(value = 0, message = "VALIDATE_ERROR")
    @Max(value = 100, message = "VALIDATE_ERROR")
    Integer percentWeekend;

    @NotNull(message = "NOT_BLANK")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime timeBeginToChange;
}
