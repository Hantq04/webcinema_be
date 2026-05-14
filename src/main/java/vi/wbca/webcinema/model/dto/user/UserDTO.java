package vi.wbca.webcinema.model.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import vi.wbca.webcinema.validation.groupValidate.user.InsertUser;
import vi.wbca.webcinema.validation.groupValidate.user.LoginUser;
import vi.wbca.webcinema.validation.groupValidate.user.UpdateUser;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    @NotNull(message = "NOT_BLANK", groups = {UpdateUser.class})
    Integer point;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Size(min = 3, max = 20, message = "SIZE_RANGE", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
//    @Pattern(regexp = "^[A-Za-z0-9]{3,20}$", message = "INVALID_USERNAME_FORM", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    String userName;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Size(min = 6, max = 30, message = "SIZE_RANGE", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "INVALID_EMAIL_FORM", groups = {InsertUser.class, UpdateUser.class})
    String email;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Size(min = 3, max = 20, message = "SIZE_RANGE", groups = {InsertUser.class, UpdateUser.class})
    String name;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^0\\d{9}$", message = "INVALID_PHONE_FORM", groups = {InsertUser.class, UpdateUser.class})
    String phoneNumber;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    LocalDate birthDate;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    String gender;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Size(min = 6, max = 20, message = "SIZE_RANGE", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
            message = "INVALID_PASSWORD_FORM",
            groups = {InsertUser.class, UpdateUser.class}
    )
    String password;

    @Schema(description = "ID của captcha")
    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    private String captchaId;

    @Schema(description = "Mã xác thực của captcha")
    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    private String captchaValue;

    private String role;
}
