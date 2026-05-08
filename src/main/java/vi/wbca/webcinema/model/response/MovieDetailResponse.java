package vi.wbca.webcinema.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailResponse {
    @Schema(description = "ID của phim")
    private Long id;

    @Schema(description = "Mã của phim")
    private String code;

    @Schema(description = "Tên phim")
    private String name;

    @Schema(description = "Tên phim tiếng Anh")
    private String nameEn;

    @Schema(description = "Ảnh bìa của phim")
    private String image;

    @Schema(description = "ID của banner")
    private Long bannerId;

    @Schema(description = "Đạo diễn của phim")
    private String director;

    @Schema(description = "Diễn viên của phim")
    private String actor;

    @Schema(description = "Danh sách thể loại của phim")
    private List<String> movieType;

    @Schema(description = "Danh sách thể loại tiếng Anh của phim")
    private List<String> movieTypeEn;

    @Schema(description = "Ngày công chiếu của phim")
    private String premiereDate;

    @Schema(description = "Ngày kết thúc chiếu của phim")
    private String endDate;

    @Schema(description = "Thời lượng của phim")
    private Integer duration;

    @Schema(description = "Ngôn ngữ của phim")
    private String language;

    @Schema(description = "Phụ đề của phim")
    private String subtitle;

    @Schema(description = "Nhãn của phim")
    private String rate;

    @Schema(description = "Nhãn tiếng Anh của phim")
    private String rateEn;

    @Schema(description = "Tên nhãn của phim")
    private String rateName;

    @Schema(description = "Tên nhãn tiếng Anh của phim")
    private String rateNameEn;

    @Schema(description = "Đường dẫn đến trailer của phim")
    private String trailerUrl;

    @Schema(description = "Mô tả của phim")
    private String description;

    @Schema(description = "Mô tả tiếng Anh của phim")
    private String descriptionEn;
}
