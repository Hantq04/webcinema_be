package vi.wbca.webcinema.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import vi.wbca.webcinema.validation.ValidUploadImagePath;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    @Schema(description = "Email")
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 30, message = "SIZE_RANGE")
    @Email(message = "INVALID_EMAIL_FORM")
    private String email;

    @Schema(description = "Tên hiển thị")
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "SIZE_RANGE")
    private String name;

    @Schema(description = "Số điện thoại")
    @NotBlank(message = "NOT_BLANK")
    @Pattern(regexp = "^0\\d{9}$", message = "INVALID_PHONE_FORM")
    private String phoneNumber;

    @Schema(description = "Địa chỉ")
    private String address;

    @Schema(description = "Thành phố")
    private String city;

    @Schema(description = "Quận/Huyện")
    private String district;

    @Schema(description = "Giới tính")
    @NotBlank(message = "NOT_BLANK")
    private String gender;

    @Schema(description = "Ngày sinh")
    @NotNull(message = "NOT_BLANK")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "Ảnh avatar")
    @ValidUploadImagePath
    private MultipartFile file;
}