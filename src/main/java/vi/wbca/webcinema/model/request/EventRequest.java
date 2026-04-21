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
public class EventRequest {
    @Schema(description = "Đường dẫn đến hình ảnh event")
    @ValidUploadImagePath
    private MultipartFile file;

    @Schema(description = "Tên event")
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 100, message = "SIZE_RANGE")
    private String name;
}