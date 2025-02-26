package vi.wbca.webcinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
    Long id;

    @NotNull(message = "NOT_NULL")
    Integer point;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_USERNAME")
    @Pattern(regexp = "^[A-Za-z0-9]{3,20}$", message = "INVALID_USERNAME_FORM")
    String userName;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 20, message = "INVALID_EMAIL")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "INVALID_EMAIL_FORM")
    String email;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 20, message = "INVALID_NAME")
    String name;

    @NotBlank(message = "NOT_BLANK")
    @Pattern(regexp = "^0\\d{9}$", message = "INVALID_PHONE_FORM")
    String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 6, max = 20, message = "INVALID_PASSWORD")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
            message = "INVALID_PASSWORD_FORM"
    )
    String password;

    List<Bill> bills;

    List<ConfirmEmail> confirmEmails;

    List<RefreshToken> refreshTokens;

    List<String> listRoles;

    List<Role> roleNames;
}
