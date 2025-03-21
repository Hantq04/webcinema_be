package vi.wbca.webcinema.dto.cinema;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
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
    @Pattern(
            regexp = "^(?:[a-zA-Z]:\\\\|/)?(?:[^\\/:*?\"<>|]+[/\\\\])*[^\\/:*?\"<>|]+\\.(jpg|jpeg|png|gif|bmp)$",
            message = "INVALID_IMAGE_PATH"
    )
    String image;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_NAME")
    String nameOfFood;
}
