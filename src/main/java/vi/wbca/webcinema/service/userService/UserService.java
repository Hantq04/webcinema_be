package vi.wbca.webcinema.service.userService;

import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.model.User;

import java.util.List;

public interface UserService {
    void register(UserDTO userDTO);

    UserDTO login(UserDTO userDTO);

    List<User> getAllUser();

    User findByUserName(String userName);
}
