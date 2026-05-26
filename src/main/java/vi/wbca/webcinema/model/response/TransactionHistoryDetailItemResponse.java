package vi.wbca.webcinema.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryDetailItemResponse {
    @Schema(description = "Tên rạp")
    private String cinemaName;

    @Schema(description = "Tên phòng")
    private String roomCode;

    @Schema(description = "Giờ suất chiếu")
    private String showTime;

    @Schema(description = "Mã ghế")
    private String seatCode;

    @Schema(description = "Đơn giá")
    private BigDecimal unitPrice;
}