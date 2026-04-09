package vi.wbca.webcinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    @Schema(description = "ID lịch chiếu")
    private Long id;

    @Schema(description = "Tên rạp phim")
    private String cinema;

    @Schema(description = "Tên phim")
    private String movie;

    @Schema(description = "Thời gian bắt đầu chiếu phim")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime startAt;

    @Schema(description = "Thời gian kết thúc chiếu phim")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime endAt;

    @Schema(description = "Mã lịch chiếu")
    private String code;

    @Schema(description = "Tên lịch chiếu")
    private String name;

    @Schema(description = "Trạng thái hoạt động của lịch chiếu")
    private boolean isActive;
}
