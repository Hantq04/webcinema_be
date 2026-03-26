package vi.wbca.webcinema.model.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.validation.groupValidate.user.InsertUser;
import vi.wbca.webcinema.validation.groupValidate.user.LoginUser;
import vi.wbca.webcinema.validation.groupValidate.user.UpdateUser;
import vi.wbca.webcinema.model.entity.user.Role;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    @NotNull(message = "NOT_BLANK", groups = {UpdateUser.class})
    Integer point;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Size(min = 3, max = 20, message = "INVALID_USERNAME", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Pattern(regexp = "^[A-Za-z0-9]{3,20}$", message = "INVALID_USERNAME_FORM", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    String userName;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Size(min = 6, max = 30, message = "INVALID_EMAIL", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "INVALID_EMAIL_FORM", groups = {InsertUser.class, UpdateUser.class})
    String email;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Size(min = 3, max = 20, message = "INVALID_NAME", groups = {InsertUser.class, UpdateUser.class})
    String name;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^0\\d{9}$", message = "INVALID_PHONE_FORM", groups = {InsertUser.class, UpdateUser.class})
    String phoneNumber;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Size(min = 6, max = 20, message = "INVALID_PASSWORD", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
            message = "INVALID_PASSWORD_FORM",
            groups = {InsertUser.class, UpdateUser.class}
    )
    String password;
}
