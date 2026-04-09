package vi.wbca.webcinema.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "NOT_BLANK")
    private String oldPassword;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, message = "PASSWORD_MIN_6")
    private String newPassword;

    @NotBlank(message = "NOT_BLANK")
    private String confirmPassword;
}
