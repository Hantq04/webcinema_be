package vi.wbca.webcinema.model.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.enums.RoomTypeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_BLANK")
    @Min(value = 50, message = "INVALID_CAPACITY")
    @Max(value = 550, message = "INVALID_CAPACITY")
    Integer capacity;

    @Enumerated(EnumType.STRING)
    RoomTypeEnum type;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 50, message = "SIZE_RANGE")
    String description;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 2, max = 10, message = "SIZE_RANGE")
    String code;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "SIZE_RANGE")
    String name;

    Boolean isActive;
}
