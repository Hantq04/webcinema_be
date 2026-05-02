package vi.wbca.webcinema.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieTypeResponse {
    @Schema(description = "ID của thể loại phim")
    private Long id;

    @Schema(description = "Tên thể loại phim tiếng Việt")
    private String movieTypeNameVi;

    @Schema(description = "Tên thể loại phim tiếng Anh")
    private String movieTypeNameEn;

    @Schema(description = "Trạng thái hoạt động")
    private boolean isActive;
}