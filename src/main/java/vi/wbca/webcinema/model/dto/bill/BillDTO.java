package vi.wbca.webcinema.model.dto.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    BigDecimal totalMoney;

    String tradingCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date createTime;

    String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date updateTime;

    @NotBlank(message = "NOT_BLANK")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String customerName;

    @NotEmpty(message = "NOT_BLANK")
    List<BillFoodDTO> foods;

    @NotEmpty(message = "NOT_BLANK")
    List<String> tickets;

    String promotionCode;
}
