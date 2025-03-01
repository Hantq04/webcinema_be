package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vi.wbca.webcinema.groupValidate.InsertUser;
import vi.wbca.webcinema.groupValidate.LoginUser;
import vi.wbca.webcinema.groupValidate.UpdateUser;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.ConfirmEmail;
import vi.wbca.webcinema.model.RefreshToken;
import vi.wbca.webcinema.model.Role;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;

    @NotNull(message = "NOT_NULL", groups = {InsertUser.class, UpdateUser.class})
    @Min(value = 0, message = "POINT_TOO_LOW")
    @Max(value = 100, message = "POINT_TOO_HIGH")
    Integer point;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Size(min = 3, max = 20, message = "INVALID_USERNAME", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Pattern(regexp = "^[A-Za-z0-9]{3,20}$", message = "INVALID_USERNAME_FORM", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    String userName;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Size(min = 6, max = 20, message = "INVALID_EMAIL", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "INVALID_EMAIL_FORM", groups = {InsertUser.class, UpdateUser.class})
    String email;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Size(min = 3, max = 20, message = "INVALID_NAME", groups = {InsertUser.class, UpdateUser.class})
    String name;

    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, UpdateUser.class})
    @Pattern(regexp = "^0\\d{9}$", message = "INVALID_PHONE_FORM", groups = {InsertUser.class, UpdateUser.class})
    String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "NOT_BLANK", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Size(min = 6, max = 20, message = "INVALID_PASSWORD", groups = {InsertUser.class, LoginUser.class, UpdateUser.class})
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
            message = "INVALID_PASSWORD_FORM",
            groups = {InsertUser.class, LoginUser.class, UpdateUser.class}
    )
    String password;

//    List<Bill> bills;
//
//    List<ConfirmEmail> confirmEmails;
//
//    List<RefreshToken> refreshTokens;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(message = "NOT_EMPTY", groups = {InsertUser.class, UpdateUser.class})
    List<String> listRoles;

    String token;

    List<Role> roleNames;
}
