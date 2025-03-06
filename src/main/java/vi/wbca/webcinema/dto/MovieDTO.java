package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_NULL")
    Integer movieDuration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date endTime;

    @NotNull(message = "NOT_NULL")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Date premiereDate;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 50, message = "INVALID_DESCRIPTION")
    String description;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_DIRECTOR")
    String director;

    @NotBlank(message = "NOT_BLANK")
    @Pattern(
            regexp = "^(?:[a-zA-Z]:\\\\|/)?(?:[^\\/:*?\"<>|]+[/\\\\])*[^\\/:*?\"<>|]+\\.(jpg|jpeg|png|gif|bmp)$",
            message = "INVALID_IMAGE_PATH"
    )
    String image;

    @NotBlank(message = "NOT_BLANK")
    @Pattern(
            regexp = "^(?:[a-zA-Z]:\\\\|/)?(?:[^\\/:*?\"<>|]+[/\\\\])*[^\\/:*?\"<>|]+\\.(jpg|jpeg|png|gif|bmp)$",
            message = "INVALID_IMAGE_PATH"
    )
    String heroImage;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_LANGUAGE")
    String language;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_NAME")
    String name;

    @NotBlank(message = "NOT_BLANK")
    @URL(message = "INVALID_TRAILER_FORM")
    String trailer;

    String movieTypeName;

    String code;
}
