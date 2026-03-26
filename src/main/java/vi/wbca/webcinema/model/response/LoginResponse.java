package vi.wbca.webcinema.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vi.wbca.webcinema.model.entity.user.Role;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "Tên người dùng")
    String userName;

    @Schema(description = "Vai trò của người dùng")
    String role;

    @Schema(description = "Token truy cập")
    String accessToken;

    @Schema(description = "Thời gian hết hạn của token")
    Long expiresIn;
}
