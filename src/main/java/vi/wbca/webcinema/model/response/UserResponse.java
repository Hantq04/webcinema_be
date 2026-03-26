package vi.wbca.webcinema.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vi.wbca.webcinema.model.entity.user.Role;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @Schema(description = "ID người dùng")
    String id;

    @Schema(description = "Tên người dùng")
    String userName;

    @Schema(description = "Địa chỉ email")
    String email;

    @Schema(description = "Tên đầy đủ")
    String name;

    @Schema(description = "Số điện thoại")
    String phoneNumber;

    @Schema(description = "Điểm tích lũy")
    Integer point;

    @Schema(description = "Vai trò của người dùng")
    String role;
}
