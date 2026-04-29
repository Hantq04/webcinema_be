package vi.wbca.webcinema.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSeatMapResponse {
    private String cinemaName;
    private String roomCode;
    private String roomName;
    private Integer capacity;
    private List<SeatResponse> seats;
}
