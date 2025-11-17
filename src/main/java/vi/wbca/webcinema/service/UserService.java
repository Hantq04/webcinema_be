package vi.wbca.webcinema.service;

import vi.wbca.webcinema.dto.user.UserDTO;

import java.util.List;

public interface UserService {
    void register(UserDTO userDTO);

    UserDTO login(UserDTO userDTO);

    void updateUser(UserDTO userDTO);

    void deleteUser(List<String> listUsers);

    List<UserDTO> getAllUser();

    UserDTO findById(Long id);
}
