package vi.wbca.webcinema.dto;

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

    @NotBlank(message = "NOT_BLANK")
    String name;
}
