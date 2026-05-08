package vi.wbca.webcinema.model.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatRefreshRequest {
    @NotBlank(message = "NOT_BLANK")
    String roomCode;

    @NotEmpty(message = "SEAT_EMPTY")
    List<Long> seatIds;
}