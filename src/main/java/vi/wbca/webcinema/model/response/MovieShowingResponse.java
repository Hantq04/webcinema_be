package vi.wbca.webcinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieShowingResponse {
    @Schema(description = "ID của phim")
    Long id;

    @Schema(description = "Mã của phim")
    String code;

    @Schema(description = "Tên phim")
    String name;

    @Schema(description = "Tên phim tiếng Anh")
    String nameEn;

    @Schema(description = "Ảnh bìa của phim")
    String image;

    @Schema(description = "Nhãn của phim")
    String rate;

    @Schema(description = "Nhãn tiếng Anh của phim")
    String rateEn;

    @Schema(description = "Danh sách thể loại của phim")
    List<String> movieType;

    @Schema(description = "Danh sách thể loại tiếng Anh của phim")
    List<String> movieTypeEn;

    @Schema(description = "Thời lượng của phim")
    Integer duration;

    @Schema(description = "Ngày công chiếu của phim")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime premiereDate;
}
