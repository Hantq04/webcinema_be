package vi.wbca.webcinema.service.userService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.enums.EUserStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.UserMapper;
import vi.wbca.webcinema.model.Role;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.model.UserStatus;
import vi.wbca.webcinema.repository.RoleRepo;
import vi.wbca.webcinema.repository.UserRepo;
import vi.wbca.webcinema.repository.UserStatusRepo;
import vi.wbca.webcinema.util.Informations;
import vi.wbca.webcinema.util.jwt.JwtTokenProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepo userRepo;
    UserMapper userMapper;
    RoleRepo roleRepo;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtTokenProvider jwtTokenProvider;
    UserStatusRepo userStatusRepo;

    @Override
    public void register(UserDTO request) {
        if (userRepo.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);
        userStatus(user);
        request.getListRoles().forEach(role -> addRole(role, user));
    }

    public void userStatus(User user) {
        if (!user.isActive()) {
            UserStatus userStatus = userStatusRepo.findByCode(EUserStatus.INACTIVE.name())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_USER_STATUS));
            user.setUserStatus(userStatus);
            userRepo.save(user);
        }
    }

    public void addRole(String role, User user) {
        switch (role) {
            case Informations.ADMIN -> roleRepo.save(Role.builder()
                    .roleName(Informations.ROLE_ADMIN_NAME)
                    .code(Informations.ADMIN)
                    .user(user).build());
            case Informations.USER -> roleRepo.save(Role.builder()
                    .roleName(Informations.ROLE_USER_NAME)
                    .code(Informations.USER)
                    .user(user).build());
            default -> throw new AppException(ErrorCode.INVALID_ROLE);
        }
    }

    @Override
    public UserDTO login(UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUserName(),
                        userDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails);
        User user = userRepo.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new  AppException(ErrorCode.USERNAME_NOT_FOUND));
        UserDTO response = userMapper.toUserDTO(user);
        response.setToken(jwt);
        return response;
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
