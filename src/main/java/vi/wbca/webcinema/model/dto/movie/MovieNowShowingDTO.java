package vi.wbca.webcinema.model.dto.movie;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieNowShowingDTO {
    @Schema(description = "ID của phim")
    Long id;

    @Schema(description = "Mã của phim")
    String code;

    @Schema(description = "Tên phim")
    String name;

    @Schema(description = "Ảnh bìa của phim")
    String image;

    @Schema(description = "Trailer của phim")
    String trailer;

    @Schema(description = "Nhãn của phim")
    String rate;
}