package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_NULL")
    @Min(value = 1000, message = "INVALID_PRICE")
    Double price;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 50, message = "INVALID_DESCRIPTION")
    String description;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 4, max = 30, message = "INVALID_IMAGE")
    String image;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_NAME")
    String nameOfFood;
}
