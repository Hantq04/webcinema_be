package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.model.Room;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_NULL")
    @Min(value = 1, message = "INVALID_NUMBER_SEAT")
    @Max(value = 20, message = "INVALID_NUMBER_SEAT")
    Integer number;

    @NotBlank(message = "NOT_BLANK")
    @Pattern(regexp = "^[A-Z]$", message = "INVALID_LINE")
    String line;

    @NotBlank(message = "NOT_BLANK")
    String roomCode;
}
