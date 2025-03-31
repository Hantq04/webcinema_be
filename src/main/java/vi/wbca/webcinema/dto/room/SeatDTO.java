package vi.wbca.webcinema.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.groupValidate.seat.InsertSeat;
import vi.wbca.webcinema.groupValidate.seat.UpdateSeat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_NULL", groups = {UpdateSeat.class})
    @Min(value = 1, message = "INVALID_NUMBER_SEAT", groups = {UpdateSeat.class})
    @Max(value = 20, message = "INVALID_NUMBER_SEAT", groups = {UpdateSeat.class})
    Integer number;

    @NotBlank(message = "NOT_BLANK", groups = {UpdateSeat.class})
    @Pattern(regexp = "^[A-Z]$", message = "INVALID_LINE", groups = {UpdateSeat.class})
    String line;

    @NotBlank(message = "NOT_BLANK", groups = {InsertSeat.class, UpdateSeat.class})
    String roomName;

    @NotBlank(message = "NOT_BLANK", groups = {InsertSeat.class, UpdateSeat.class})
    String roomCode;
}
