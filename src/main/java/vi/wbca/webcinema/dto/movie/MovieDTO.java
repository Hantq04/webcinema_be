package vi.wbca.webcinema.dto.movie;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;
import vi.wbca.webcinema.groupValidate.movie.InsertMovie;
import vi.wbca.webcinema.groupValidate.movie.UpdateMovie;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_NULL", groups = {InsertMovie.class, UpdateMovie.class})
    Integer movieDuration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date endDate;

    @NotNull(message = "NOT_NULL", groups = {InsertMovie.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date premiereDate;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 6, max = 50, message = "INVALID_DESCRIPTION", groups = {InsertMovie.class, UpdateMovie.class})
    String description;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 20, message = "INVALID_DIRECTOR", groups = {InsertMovie.class, UpdateMovie.class})
    String director;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Pattern(
            regexp = "^(?:[a-zA-Z]:\\\\|/)?(?:[^\\/:*?\"<>|]+[/\\\\])*[^\\/:*?\"<>|]+\\.(jpg|jpeg|png|gif|bmp)$",
            message = "INVALID_IMAGE_PATH", groups = {InsertMovie.class, UpdateMovie.class}
    )
    String image;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Pattern(
            regexp = "^(?:[a-zA-Z]:\\\\|/)?(?:[^\\/:*?\"<>|]+[/\\\\])*[^\\/:*?\"<>|]+\\.(jpg|jpeg|png|gif|bmp)$",
            message = "INVALID_IMAGE_PATH", groups = {InsertMovie.class, UpdateMovie.class}
    )
    String heroImage;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 20, message = "INVALID_LANGUAGE", groups = {InsertMovie.class, UpdateMovie.class})
    String language;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @Size(min = 3, max = 20, message = "INVALID_NAME", groups = {InsertMovie.class, UpdateMovie.class})
    String name;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    @URL(message = "INVALID_TRAILER_FORM", groups = {InsertMovie.class, UpdateMovie.class})
    String trailer;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class})
    String movieTypeName;

    @NotBlank(message = "NOT_BLANK", groups = {InsertMovie.class, UpdateMovie.class})
    String code;
}
