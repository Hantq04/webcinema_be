package vi.wbca.webcinema.model.dto.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.enums.PromotionTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_BLANK")
    Integer percent;

    @NotNull(message = "NOT_BLANK")
    Integer quantity;

    @Enumerated(EnumType.STRING)
    PromotionTypeEnum promotionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime endTime;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 50, message = "SIZE_RANGE")
    String description;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "SIZE_RANGE")
    String name;

    @NotBlank(message = "NOT_BLANK")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String nameRankCustomer;
}
