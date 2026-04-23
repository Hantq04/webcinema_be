package vi.wbca.webcinema.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMovieFilterRequest {
    @Schema(description = "ID của phim")
    @NotNull(message = "NOT_BLANK")
    private Long movieId;

    @Schema(description = "Ngày chiếu", example = "2026-04-23")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "Địa chỉ rạp")
    private String address;

    @Schema(description = "Loại phòng")
    private String roomType;
}