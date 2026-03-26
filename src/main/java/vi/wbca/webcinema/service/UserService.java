package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.user.UserDTO;
import vi.wbca.webcinema.model.request.LoginRequest;
import vi.wbca.webcinema.model.response.LoginResponse;
import vi.wbca.webcinema.model.response.UserResponse;

import java.util.List;

public interface UserService {
    void register(UserDTO userDTO);

    void staffRegister(UserDTO userDto);

    LoginResponse login(LoginRequest request);

    void updateUser(UserDTO userDTO);

    void deleteUser(List<String> listUsers);

    List<UserResponse> getAllUser();

    UserDTO findById(Long id);
}
