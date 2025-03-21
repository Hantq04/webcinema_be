package vi.wbca.webcinema.dto.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketDTO {
    String code;

    Double priceTicket;

    @NotBlank(message = "NOT_BLANK")
    String roomName;

    @NotBlank(message = "NOT_BLANK")
    String roomCode;

    @NotNull(message = "NOT_NULL")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date startTime;

    @NotBlank(message = "NOT_BLANK")
    String selectSeat;
}
