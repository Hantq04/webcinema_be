package vi.wbca.webcinema.model.response;

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
    String userName;
    List<Role> role;
    String accessToken;
    Long expiresIn;
}
