package vi.wbca.webcinema.dto.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillTicketDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Integer quantity;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "NOT_BLANK")
    String customerName;

    @NotBlank(message = "NOT_BLANK")
    String code;
}
