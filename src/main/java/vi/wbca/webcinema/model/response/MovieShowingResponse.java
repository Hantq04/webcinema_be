package vi.wbca.webcinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieShowingResponse {
    @Schema(description = "ID của phim")
    Long id;

    @Schema(description = "Tên phim")
    String name;

    @Schema(description = "Ảnh bìa của phim")
    String image;

    @Schema(description = "Nhãn của phim")
    String rate;

    @Schema(description = "Thể loại của phim")
    String movieType;

    @Schema(description = "Thời lượng của phim")
    Integer duration;

    @Schema(description = "Ngày công chiếu của phim")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime premiereDate;
}
