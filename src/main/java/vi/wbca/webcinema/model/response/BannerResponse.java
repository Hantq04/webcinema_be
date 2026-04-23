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
public class BannerResponse {
    @Schema(description = "ID của banner")
    private Long id;

    @Schema(description = "URL của hình ảnh banner")
    private String imageUrl;

    @Schema(description = "Tiêu đề của banner")
    private String title;

    @Schema(description = "Tiêu đề tiếng Anh của banner")
    private String titleEn;
}
