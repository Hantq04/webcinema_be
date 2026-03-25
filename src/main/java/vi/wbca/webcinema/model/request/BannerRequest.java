package vi.wbca.webcinema.model.request;

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
    @ValidUploadImagePath
    private MultipartFile file;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_NAME")
    private String title;
}
