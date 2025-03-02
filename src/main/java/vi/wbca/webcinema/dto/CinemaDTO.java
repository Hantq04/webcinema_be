package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.groupValidate.cinema.InsertCinema;
import vi.wbca.webcinema.groupValidate.cinema.UpdateCinema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotBlank(message = "NOT_BLANK", groups = {InsertCinema.class, UpdateCinema.class})
    @Size(min = 4, max = 30, message = "INVALID_ADDRESS", groups = {InsertCinema.class, UpdateCinema.class})
    String address;

    @NotBlank(message = "NOT_BLANK", groups = {InsertCinema.class, UpdateCinema.class})
    @Size(min = 6, max = 50, message = "INVALID_DESCRIPTION", groups = {InsertCinema.class, UpdateCinema.class})
    String description;

    String code;

    @NotBlank(message = "NOT_BLANK", groups = {InsertCinema.class, UpdateCinema.class})
    @Size(min = 3, max = 20, message = "INVALID_NAME", groups = {InsertCinema.class, UpdateCinema.class})
    String nameOfCinema;
}
