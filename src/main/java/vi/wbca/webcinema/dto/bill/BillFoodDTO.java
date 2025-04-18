package vi.wbca.webcinema.dto.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillFoodDTO {
    @NotNull(message = "NOT_NULL")
    Integer quantity;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "NOT_BLANK")
    String customerName;

    @NotBlank(message = "NOT_BLANK")
    String name;
}
