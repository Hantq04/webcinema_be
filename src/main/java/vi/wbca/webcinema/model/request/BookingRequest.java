package vi.wbca.webcinema.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @Schema(description = "Tên phòng chiếu")
    private String roomName;

    @Schema(description = "Mã phòng chiếu")
    private String roomCode;

    @Schema(description = "Thời gian bắt đầu chiếu phim")
    private Date startTime;

    @Schema(description = "Danh sách ghế được đặt")
    private List<String> seats;
}
