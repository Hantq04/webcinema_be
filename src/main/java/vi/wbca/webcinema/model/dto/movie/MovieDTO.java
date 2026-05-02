package vi.wbca.webcinema.model.dto.movie;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;
import vi.wbca.webcinema.validation.groupValidate.movie.InsertMovie;
import vi.wbca.webcinema.validation.groupValidate.movie.UpdateMovie;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    Integer movieDuration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime endDate;

    @NotNull(message = "NOT_BLANK", groups = {InsertMovie.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime premiereDate;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 6, max = 5000, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String description;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 30, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String director;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 200, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String actor;

    @NotNull(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    Long bannerId;

    String image;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 20, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String language;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 2, max = 100, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String subtitle;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 50, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String name;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 50, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String nameEn;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 6, max = 5000, message = "SIZE_RANGE", groups = {InsertMovie.class, UpdateMovie.class})
    String descriptionEn;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @URL(message = "INVALID_TRAILER_FORM", groups = {InsertMovie.class, UpdateMovie.class})
    String trailer;

    @NotNull(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 1, message = "NOT_EMPTY", groups = {InsertMovie.class, UpdateMovie.class})
    List<Long> movieTypeIds;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    String code;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    String rate;
}
