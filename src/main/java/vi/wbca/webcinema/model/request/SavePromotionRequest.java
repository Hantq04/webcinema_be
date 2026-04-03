package vi.wbca.webcinema.model.request;

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
    @NotNull(message = "NOT_BLANK")
    Long userId;
    
    @NotBlank(message = "NOT_BLANK")
    String promotionCode;
}
