package vi.wbca.webcinema.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @Schema(description = "Tên phòng chiếu")
    @NotBlank(message = "NOT_BLANK")
    private String roomName;

    @Schema(description = "Mã phòng chiếu")
    @NotBlank(message = "NOT_BLANK")
    private String roomCode;

    @Schema(description = "Thời gian bắt đầu chiếu phim")
    @NotNull(message = "NOT_BLANK")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime startTime;

    @Schema(description = "Danh sách ghế được đặt")
    @NotEmpty(message = "NOT_EMPTY")
    private List<String> seats;
}
