package vi.wbca.webcinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponse {
    @Schema(description = "ID hóa đơn")
    private Long id;

    @Schema(description = "Mã giao dịch")
    private String tradingCode;

    @Schema(description = "Tên khách hàng")
    private String customerName;

    @Schema(description = "Mã rạp")
    private String cinemaCode;

    @Schema(description = "Tên rạp")
    private String cinemaName;

    @Schema(description = "Mã phòng chiếu")
    private String roomCode;

    @Schema(description = "Tên phòng chiếu")
    private String roomName;

    @Schema(description = "Tên phim")
    private String movieName;

    @Schema(description = "Tên suất chiếu")
    private String showTimeName;

    @Schema(description = "Trạng thái hóa đơn")
    private String billStatus;

    @Schema(description = "Tổng tiền")
    private BigDecimal totalMoney;

    @Schema(description = "Số vé trong hóa đơn")
    private Integer ticketCount;

    @Schema(description = "Số món ăn trong hóa đơn")
    private Integer foodCount;

    @Schema(description = "Thời gian tạo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createTime;

    @Schema(description = "Thời gian cập nhật")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime updateTime;
}