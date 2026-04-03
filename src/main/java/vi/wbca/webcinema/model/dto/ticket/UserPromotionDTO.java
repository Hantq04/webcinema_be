package vi.wbca.webcinema.model.dto.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.enums.PromotionTypeEnum;
import vi.wbca.webcinema.enums.VoucherStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPromotionDTO {
    @Schema(description = "ID của người dùng")
    Long userId;

    @Schema(description = "ID của khuyến mãi")
    Long promotionId;

    @Schema(description = "Mã khuyến mãi đã được sử dụng hay chưa")
    Boolean isUsed;

    @Schema(description = "Thời gian sử dụng mã khuyến mãi")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime usedAt;

    @Schema(description = "Thời gian lưu mã khuyến mãi cho người dùng")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime saveAt;

    @Schema(description = "Mã khuyến mãi")
    String promotionCode;

    @Schema(description = "Tên mã khuyến mãi")
    String promotionName;

    @Schema(description = "Phần trăm mã khuyến mãi")
    Integer promotionPercent;

    @Schema(description = "Loại mã khuyến mãi")
    PromotionTypeEnum promotionType;

    @Schema(description = "Thời gian bắt đầu của mã khuyến mãi")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime promotionStartTime;

    @Schema(description = "Thời gian kết thúc của mã khuyến mãi")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime promotionEndTime;

    @Schema(description = "Mô tả của mã khuyến mãi")
    String promotionDescription;

    @Schema(description = "Trạng thái hoạt động của mã khuyến mãi")
    Boolean promotionActive;

    @Schema(description = "Trạng thái của mã khuyến mãi")
    VoucherStatusEnum voucherStatus;
}
