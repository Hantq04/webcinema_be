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
import vi.wbca.webcinema.model.entity.user.ChangeTypeEnum;
import vi.wbca.webcinema.model.entity.user.Role;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.entity.user.UserChangeHistory;
import vi.wbca.webcinema.model.entity.user.UserProfile;
import vi.wbca.webcinema.model.entity.user.UserStatus;
import vi.wbca.webcinema.model.request.LoginRequest;
import vi.wbca.webcinema.model.request.UserProfileUpdateRequest;
import vi.wbca.webcinema.model.response.LoginResponse;
import vi.wbca.webcinema.model.response.UserProfileResponse;
import vi.wbca.webcinema.model.response.UserResponse;
import vi.wbca.webcinema.repository.user.RankCustomerRepo;
import vi.wbca.webcinema.repository.user.RoleRepo;
import vi.wbca.webcinema.repository.user.UserChangeHistoryRepo;
import vi.wbca.webcinema.repository.user.UserProfileRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.repository.user.UserStatusRepo;
import vi.wbca.webcinema.service.*;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.ImageUtils;
import vi.wbca.webcinema.util.jwt.JwtTokenProvider;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.time.LocalDateTime;
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
    CaptchaService captchaService;
    RefreshTokenService refreshTokenService;
    UserProfileRepo userProfileRepo;
    UserChangeHistoryRepo userChangeHistoryRepo;

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
        // Validate the CAPTCHA
//        captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaValue());

        User user = userRepo.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        boolean changedRecently = userChangeHistoryRepo.existsByUserAndChangeTypeAndChangedAtAfter(
                user, ChangeTypeEnum.PASSWORD_CHANGE, LocalDateTime.now().minusDays(30));

        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(), request.getPassWord()
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
                    .userId(user.getId())
                    .userName(user.getUsername())
                    .role(user.getRole().toString())
                    .accessToken(jwt)
                    .expiresIn(accessToken.getExpiresIn())
                    .build();
        } catch (BadCredentialsException ex) {
            if (changedRecently) {
                throw new AppException(ErrorCode.PASSWORD_CHANGED_RECENTLY);
            }
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public void updateProfile(UserProfileUpdateRequest request) {
        User currentUser = getCurrentUser();
        UserProfile profile = getRequiredProfile(currentUser);

        if (userProfileRepo.existsByEmailAndUserIdNotAndUserIsActiveTrue(request.getEmail(), currentUser.getId())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userProfileRepo.existsByPhoneNumberAndUserIdNotAndUserIsActiveTrue(request.getPhoneNumber(), currentUser.getId())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        UserChangeHistory history = UserChangeHistory.builder()
                .user(currentUser)
                .changeType(ChangeTypeEnum.PROFILE_UPDATE)
                .oldName(profile.getName())
                .newName(request.getName())
                .oldEmail(profile.getEmail())
                .newEmail(request.getEmail())
                .oldPhoneNumber(profile.getPhoneNumber())
                .newPhoneNumber(request.getPhoneNumber())
                .passwordChanged(false)
                .build();
        userChangeHistoryRepo.save(history);

        profile.setEmail(request.getEmail());
        profile.setName(request.getName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setDistrict(request.getDistrict());
        profile.setGender(request.getGender());
        profile.setBirthDate(request.getBirthDate());

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            profile.setAvatarUrl(saveAvatar(request.getFile()));
        }

        userProfileRepo.save(profile);
        userRepo.save(currentUser);
    }

    @Override
    public void deleteUser(List<String> listUsers) {
        listUsers.forEach(userName -> {
            User currentUser = userRepo.findByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            currentUser.getAccessTokens().forEach(accessTokenService::deleteAccessToken);
            currentUser.setActive(false);
            currentUser.setUserStatus(userStatusRepo.findByCode(UserStatusEnum.INACTIVE.toString()));
            userRepo.save(currentUser);
        });
    }

    @Override
    public List<UserResponse> getAllUser() {
        return userRepo.findAll()
                .stream().map(this::toUserResponse)
                .toList();
    }

    @Override
    public UserDTO findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        getRequiredProfile(user);
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserProfileResponse findProfileByUserName(String userName) {
        User user = userRepo.findByUserName(userName)
            .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        UserProfile profile = getRequiredProfile(user);
        return new UserProfileResponse(
            profile.getEmail(),
            profile.getName(),
            profile.getPhoneNumber(),
            profile.getAddress(),
            profile.getCity(),
            profile.getDistrict(),
            profile.getGender(),
            profile.getBirthDate(),
            profile.getAvatarUrl(),
            profile.getPoint()
        );
    }

    public void userStatusAndRank(User user) {
        if (!user.isActive()) {
            UserStatus userStatus = userStatusRepo.findByCode(UserStatusEnum.INACTIVE.toString());
            user.setUserStatus(userStatus);
        }
        RankCustomer rankCustomer = rankCustomerRepo.findByName(CustomerRankEnum.STANDARD.toString())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        user.setRankCustomer(rankCustomer);
        UserProfile profile = getRequiredProfile(user);
        profile.setPoint(0);
        userProfileRepo.save(profile);
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
        if (userRepo.existsByUserNameAndIsActiveTrue(request.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userProfileRepo.existsByEmailAndUserIsActiveTrue(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userProfileRepo.existsByPhoneNumberAndUserIsActiveTrue(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        // Validate the CAPTCHA
        captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaValue());

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepo.save(user);
        UserProfile profile = UserProfile.builder()
            .user(user)
            .name(request.getName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .birthDate(request.getBirthDate())
            .gender(request.getGender())
            .point(0)
            .build();
        userProfileRepo.save(profile);

        userStatusAndRank(user);
        userRepo.save(user);

        try {
            accountService.sendVerificationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private UserProfile getRequiredProfile(User user) {
        return userProfileRepo.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepo.findByUserName(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private String saveAvatar(org.springframework.web.multipart.MultipartFile file) {
        try {
            return ImageUtils.saveImage(file);
        } catch (IOException e) {
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

    private UserResponse toUserResponse(User user) {
        UserProfile profile = getRequiredProfile(user);
        return new UserResponse(
                String.valueOf(user.getId()),
                user.getUsername(),
                profile.getEmail(),
                profile.getName(),
                profile.getPhoneNumber(),
                profile.getPoint(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }
}
