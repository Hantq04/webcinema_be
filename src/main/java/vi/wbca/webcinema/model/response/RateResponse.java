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
public class RateResponse {
    @Schema(description = "ID của mức giá")
    private Long id;

    @Schema(description = "Mô tả mức giá tiếng Việt")
    private String descriptionVi;

    @Schema(description = "Mô tả mức giá tiếng Anh")
    private String descriptionEn;

    @Schema(description = "Mã mức giá")
    private String code;
}