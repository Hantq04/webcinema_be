package vi.wbca.webcinema.service.userService;

import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.model.User;

import java.util.List;

public interface UserService {
    User insertUser(UserDTO userDTO);
    List<User> getAllUser();

    User findByUserName(String userName);
}
