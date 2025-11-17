package vi.wbca.webcinema.service.userService;

import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.user.UserDTO;
import vi.wbca.webcinema.enums.CustomerRank;
import vi.wbca.webcinema.enums.EUserStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.UserMapper;
import vi.wbca.webcinema.model.user.RankCustomer;
import vi.wbca.webcinema.model.user.Role;
import vi.wbca.webcinema.model.user.User;
import vi.wbca.webcinema.model.user.UserStatus;
import vi.wbca.webcinema.repository.user.RankCustomerRepo;
import vi.wbca.webcinema.repository.user.RoleRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.repository.user.UserStatusRepo;
import vi.wbca.webcinema.service.accessTokenService.AccessTokenService;
import vi.wbca.webcinema.service.accountService.AccountService;
import vi.wbca.webcinema.service.refreshTokenService.RefreshTokenService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.jwt.JwtTokenProvider;

import java.io.UnsupportedEncodingException;
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
    AccountService accountService;
    RankCustomerRepo rankCustomerRepo;
    AccessTokenService accessTokenService;
    RefreshTokenService refreshTokenService;

    @Override
    public void register(UserDTO request) {
        validateRegister(request);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);
        userStatusAndRank(user);
        request.getListRoles().forEach(role -> addRole(role, user));

        try {
            accountService.sendVerificationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void validateRegister(UserDTO request) {
        if (userRepo.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
    }

    public void userStatusAndRank(User user) {
        if (!user.isActive()) {
            UserStatus userStatus = userStatusRepo.findByCode(EUserStatus.INACTIVE.toString());
            user.setUserStatus(userStatus);
        }
        user.setRankCustomer(setRankCustomer());
        user.setPoint(0);
        userRepo.save(user);
    }

    public void addRole(String role, User user) {
        switch (role) {
            case Constants.ADMIN -> roleRepo.save(Role.builder()
                    .roleName(Constants.ROLE_ADMIN_NAME)
                    .code(Constants.ADMIN)
                    .user(user).build());
            case Constants.USER -> roleRepo.save(Role.builder()
                    .roleName(Constants.ROLE_USER_NAME)
                    .code(Constants.USER)
                    .user(user).build());
            default -> throw new AppException(ErrorCode.INVALID_ROLE);
        }
    }

    public RankCustomer setRankCustomer() {
        return rankCustomerRepo.findByName(CustomerRank.STANDARD.toString())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
    }

    @Override
    public UserDTO login(UserDTO userDTO) {
        User user = userRepo.findByUserName(userDTO.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDTO.getUserName(), userDTO.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Revoked the old JWT token from the user
            accessTokenService.revokeAllUserTokens(user);

            // Generate new JWT token
            String jwt = jwtTokenProvider.generateToken(userDetails);
            UserDTO response = userMapper.toUserDTO(user);
            response.setAccessToken(jwt);

            accessTokenService.insertAccessToken(user, jwt);
            refreshTokenService.insertRefreshToken(user);

            response.setRefreshToken(refreshTokenService.getRefreshToken(user));
            return response;
        } catch (BadCredentialsException ex) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public void updateUser(UserDTO request) {
        User currentUser = userRepo.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        if (request.getPassword() != null) currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        currentUser.setEmail(request.getEmail());
        currentUser.setPhoneNumber(request.getPhoneNumber());
        userRepo.save(currentUser);

        List<String> strRole = request.getListRoles();
        if (!strRole.isEmpty()) {
            currentUser.getRoles().forEach(roleRepo::delete);
            request.getListRoles().forEach(role -> addRole(role, currentUser));
        }
    }

    @Override
    public void deleteUser(List<String> listUsers) {
        listUsers.forEach(userName -> {
            User currentUser = userRepo.findByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            currentUser.getRoles().forEach(roleRepo::delete);
            currentUser.getAccessTokens().forEach(accessTokenService::deleteAccessToken);
            userRepo.delete(currentUser);
        });
    }

    @Override
    public List<UserDTO> getAllUser() {
        return userRepo.findAll()
                .stream().map(userMapper::toUserDTO)
                .toList();
    }

    @Override
    public UserDTO findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserDTO(user);
    }
}
