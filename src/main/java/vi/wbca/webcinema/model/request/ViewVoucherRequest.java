package vi.wbca.webcinema.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import vi.wbca.webcinema.enums.PromotionTypeEnum;
import vi.wbca.webcinema.enums.VoucherStatusEnum;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewVoucherRequest {
    @Schema(description = "ID của người dùng")
    @NotNull(message = "NOT_BLANK")
    private Long userId;

    @Schema(description = "Ngày bắt đầu")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "Ngày kết thúc")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Schema(description = "Loại khuyến mãi")
    private PromotionTypeEnum promotionType;

    @Schema(description = "Trạng thái của mã khuyến mãi")
    private VoucherStatusEnum voucherStatus;
}
