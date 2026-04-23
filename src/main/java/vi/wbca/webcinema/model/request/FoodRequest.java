package vi.wbca.webcinema.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class FoodRequest {
    @Schema(description = "Ảnh của món ăn")
    @ValidUploadImagePath
    private MultipartFile file;

    @Schema(description = "Giá của món ăn")
    @NotNull(message = "NOT_BLANK")
    @Min(value = 1000, message = "INVALID_PRICE")
    private Double price;

    @Schema(description = "Mô tả món ăn")
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 50, message = "SIZE_RANGE")
    private String description;

    @Schema(description = "Tên món ăn")
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "SIZE_RANGE")
    private String nameOfFood;
}