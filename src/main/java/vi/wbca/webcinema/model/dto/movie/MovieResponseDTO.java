package vi.wbca.webcinema.model.dto.movie;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponseDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    String code;

    String name;

    String nameEn;

    String movieTypeName;

    String movieTypeNameEn;

    Integer movieDuration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime premiereDate;
}
