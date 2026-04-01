package vi.wbca.webcinema.model.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatByScheduleDTO {
    private Long id;
    private String line;
    private Integer number;
    private String status;
    private String seatType;
}
