package vi.wbca.webcinema.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import vi.wbca.webcinema.validation.ValidUploadImagePath;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {
    @Schema(description = "Đường dẫn đến hình ảnh banner")
    @ValidUploadImagePath
    private MultipartFile file;

    @Schema(description = "Tiêu đề của banner")
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 50, message = "SIZE_RANGE")
    private String title;
}
