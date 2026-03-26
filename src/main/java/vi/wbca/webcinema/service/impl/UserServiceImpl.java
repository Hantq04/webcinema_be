package vi.wbca.webcinema.service.impl;

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
import vi.wbca.webcinema.enums.RoleEnum;
import vi.wbca.webcinema.model.dto.user.UserDTO;
import vi.wbca.webcinema.enums.CustomerRankEnum;
import vi.wbca.webcinema.enums.UserStatusEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.UserMapper;
import vi.wbca.webcinema.model.entity.token.AccessToken;
import vi.wbca.webcinema.model.entity.user.RankCustomer;
import vi.wbca.webcinema.model.entity.user.Role;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.entity.user.UserStatus;
import vi.wbca.webcinema.model.request.LoginRequest;
import vi.wbca.webcinema.model.response.LoginResponse;
import vi.wbca.webcinema.model.response.UserResponse;
import vi.wbca.webcinema.repository.user.RankCustomerRepo;
import vi.wbca.webcinema.repository.user.RoleRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.repository.user.UserStatusRepo;
import vi.wbca.webcinema.service.UserService;
import vi.wbca.webcinema.service.AccessTokenService;
import vi.wbca.webcinema.service.AccountService;
import vi.wbca.webcinema.service.RefreshTokenService;
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
        registerAccount(request, RoleEnum.USER);
    }

    @Override
    public void staffRegister(UserDTO request) {
        registerAccount(request, RoleEnum.STAFF);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepo.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(), request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Revoked the old JWT token from the user
            accessTokenService.revokeAllUserTokens(user);

            // Generate new JWT token
            String jwt = jwtTokenProvider.generateToken(userDetails);
            accessTokenService.insertAccessToken(user, jwt);
//            refreshTokenService.insertRefreshToken(user);
            AccessToken accessToken = accessTokenService.findByAccessToken(jwt);
//            response.setRefreshToken(refreshTokenService.getRefreshToken(user));

            return LoginResponse.builder()
                    .userName(user.getUsername())
                    .role(user.getRole().toString())
                    .accessToken(jwt)
                    .expiresIn(accessToken.getExpiresIn())
                    .build();
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
    }

    @Override
    public void deleteUser(List<String> listUsers) {
        listUsers.forEach(userName -> {
            User currentUser = userRepo.findByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            currentUser.getAccessTokens().forEach(accessTokenService::deleteAccessToken);
            userRepo.delete(currentUser);
        });
    }

    @Override
    public List<UserResponse> getAllUser() {
        return userRepo.findAll()
                .stream().map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserDTO findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserDTO(user);
    }

    public void userStatusAndRank(User user) {
        if (!user.isActive()) {
            UserStatus userStatus = userStatusRepo.findByCode(UserStatusEnum.INACTIVE.toString());
            user.setUserStatus(userStatus);
        }
        RankCustomer rankCustomer = rankCustomerRepo.findByName(CustomerRankEnum.STANDARD.toString())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        user.setRankCustomer(rankCustomer);
        user.setPoint(0);
        userRepo.save(user);
    }

    public void addRole(String role, User user) {
        switch (role) {
            case Constants.ADMIN -> roleRepo.save(Role.builder()
                    .roleName(Constants.ROLE_ADMIN_NAME)
                    .code(Constants.ADMIN)
                    .user(user).build());
            case Constants.STAFF -> roleRepo.save(Role.builder()
                    .roleName(Constants.ROLE_STAFF_NAME)
                    .code(Constants.STAFF)
                    .user(user).build());
            case Constants.USER -> roleRepo.save(Role.builder()
                    .roleName(Constants.ROLE_USER_NAME)
                    .code(Constants.USER)
                    .user(user).build());
            default -> throw new AppException(ErrorCode.INVALID_ROLE);
        }
    }

    public void registerAccount(UserDTO request, RoleEnum role) {
        if (userRepo.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);
        userStatusAndRank(user);
        user.setRole(role);

        try {
            accountService.sendVerificationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
