package vi.wbca.webcinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryDetailResponse {
    @Schema(description = "Mã giao dịch")
    private String tradingCode;

    @Schema(description = "Trạng thái hóa đơn")
    private String billStatus;

    @Schema(description = "Tên khách hàng")
    private String customerName;

    @Schema(description = "Tổng tiền")
    private BigDecimal totalMoney;

    @Schema(description = "Thời gian tạo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createTime;

    @Schema(description = "Danh sách ghế đã đặt")
    private List<TransactionHistoryDetailItemResponse> items;
}