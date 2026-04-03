package vi.wbca.webcinema.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SavePromotionRequest {
    @Schema(description = "ID của người dùng")
    @NotNull(message = "NOT_BLANK")
    Long userId;

    @Schema(description = "Mã khuyến mãi")
    @NotBlank(message = "NOT_BLANK")
    String promotionCode;
}
