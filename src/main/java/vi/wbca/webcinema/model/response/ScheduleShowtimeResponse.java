package vi.wbca.webcinema.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleShowtimeResponse {
    @Schema(description = "Giờ chiếu")
    private String time;

    @Schema(description = "Mã lịch chiếu")
    private String scheduleCode;
}