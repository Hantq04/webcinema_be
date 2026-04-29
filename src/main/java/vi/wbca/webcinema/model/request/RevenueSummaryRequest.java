package vi.wbca.webcinema.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import vi.wbca.webcinema.enums.RevenueGroupByEnum;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueSummaryRequest {
    @Schema(description = "Ngay bat dau", example = "2026-04-01")
    @NotNull(message = "NOT_BLANK")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @Schema(description = "Ngay ket thuc", example = "2026-04-30")
    @NotNull(message = "NOT_BLANK")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    @Schema(description = "Nhom theo thoi gian", example = "DAY")
    private RevenueGroupByEnum groupBy = RevenueGroupByEnum.DAY;

    @Schema(description = "ID rap")
    private Long cinemaId;

    @Schema(description = "ID phong")
    private Long roomId;

    @Schema(description = "ID phim")
    private Long movieId;
}
