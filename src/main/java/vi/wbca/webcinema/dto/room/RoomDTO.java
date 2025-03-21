package vi.wbca.webcinema.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.enums.RoomType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_NULL")
    @Min(value = 50, message = "INVALID_CAPACITY")
    @Max(value = 400, message = "INVALID_CAPACITY")
    Integer capacity;

    @Enumerated(EnumType.STRING)
    RoomType type;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 50, message = "INVALID_DESCRIPTION")
    String description;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 2, max = 5, message = "INVALID_CODE")
    String code;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_NAME")
    String name;
}
