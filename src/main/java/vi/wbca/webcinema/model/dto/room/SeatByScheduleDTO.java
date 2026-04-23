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
    private Integer pairIndex;
    private Long priceTicket;
    private String status;
    private String seatType;

    public SeatByScheduleDTO(Long id, String line, Integer number, Integer pairIndex, String status, String seatType) {
        this.id = id;
        this.line = line;
        this.number = number;
        this.pairIndex = pairIndex;
        this.status = status;
        this.seatType = seatType;
    }
}
