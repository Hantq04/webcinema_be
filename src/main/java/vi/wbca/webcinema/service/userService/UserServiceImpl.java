package vi.wbca.webcinema.service.userService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.UserMapper;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.UserRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Override
    public User insertUser(UserDTO request) {
        User newUser = userMapper.toUser(request);
        if (userRepo.existsByUserName(newUser.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        return userRepo.save(newUser);
    }

    @Override
    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    @Override
    public User findByUserName(String userName) {
        return userRepo.findByUserName(userName).orElse(null);
    }
}
