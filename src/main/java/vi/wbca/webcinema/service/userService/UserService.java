package vi.wbca.webcinema.service.userService;

import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.model.User;

import java.util.List;

public interface UserService {
    void register(UserDTO userDTO);

    UserDTO login(UserDTO userDTO);

    void updateUser(UserDTO userDTO);

    void deleteUser(List<String> listUsers);

    List<User> getAllUser();

    UserDTO findById(Long id);
}
