package vi.wbca.webcinema.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vi.wbca.webcinema.model.entity.user.Role;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    String id;
    String userName;
    String email;
    String name;
    String phoneNumber;
    Integer point;
    List<Role> roleNames;
}
