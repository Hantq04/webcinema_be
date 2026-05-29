package vi.wbca.webcinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTransactionHistoryResponse {
    @Schema(description = "Mã vé")
    private String ticketCode;

    @Schema(description = "Trạng thái")
    private String status;

    @Schema(description = "Ảnh phim")
    private String image;

    @Schema(description = "Tên phim")
    private String movieName;

    @Schema(description = "Tên phim tiếng Anh")
    private String movieNameEn;

    @Schema(description = "Xếp hạng phim")
    private String rate;

    @Schema(description = "Ngày suất chiếu")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate showDate;

    @Schema(description = "Giờ chiếu")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startAt;

    @Schema(description = "Giờ kết thúc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endAt;

    @Schema(description = "Tên rạp phim")
    private String cinemaName;

    @Schema(description = "Mã phòng chiếu")
    private String roomCode;

    @Schema(description = "Ghế ngồi")
    private String seat;

    @Schema(description = "Tổng tiền vé")
    private BigDecimal totalMoney;
}
