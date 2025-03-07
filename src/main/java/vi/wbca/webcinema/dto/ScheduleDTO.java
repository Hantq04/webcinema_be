package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.groupValidate.schedule.InsertSchedule;
import vi.wbca.webcinema.groupValidate.schedule.UpdateSchedule;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(value = {"movieName", "roomName", "roomCode", "seatTypeId"}, allowSetters = true)
public class ScheduleDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    Double price;

    @NotNull(message = "NOT_NULL", groups = {InsertSchedule.class, UpdateSchedule.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date startAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date endAt;

    String code;

    String name;

    @NotBlank(message = "NOT_BLANK", groups = {InsertSchedule.class})
    String movieName;

    @NotBlank(message = "NOT_BLANK", groups = {InsertSchedule.class})
    String roomName;

    @NotBlank(message = "NOT_BLANK", groups = {InsertSchedule.class})
    String roomCode;

    @NotNull(message = "NOT_NULL", groups = {InsertSchedule.class})
    int seatTypeId;
}
