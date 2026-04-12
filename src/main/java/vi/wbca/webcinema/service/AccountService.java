package vi.wbca.webcinema.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.config.EmailService;
import vi.wbca.webcinema.enums.UserStatusEnum;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.entity.setting.ConfirmEmail;
import vi.wbca.webcinema.model.entity.user.ChangeTypeEnum;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.entity.user.UserChangeHistory;
import vi.wbca.webcinema.model.entity.user.UserProfile;
import vi.wbca.webcinema.model.entity.user.UserStatus;
import vi.wbca.webcinema.repository.setting.ConfirmEmailRepo;
import vi.wbca.webcinema.repository.user.UserChangeHistoryRepo;
import vi.wbca.webcinema.repository.user.UserProfileRepo;
import vi.wbca.webcinema.repository.user.UserRepo;
import vi.wbca.webcinema.repository.user.UserStatusRepo;
import vi.wbca.webcinema.util.EmailUtils;
import vi.wbca.webcinema.util.generate.GenerateOTP;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final EmailService emailService;
    private final ConfirmEmailRepo confirmEmailRepo;
    private final UserRepo userRepo;
    private final UserProfileRepo userProfileRepo;
    private final UserChangeHistoryRepo userChangeHistoryRepo;
    private final UserStatusRepo userStatusRepo;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final String generateOTP = GenerateOTP.generateOTP();

    @Value("${application.email.verify-expiration}")
    private long expiredTime;

    public void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String content = EmailUtils.getEmailMessage(getUserDisplayName(user), generateOTP);
        emailService.sendMail(getUserEmail(user), subject, content);
        createConfirmEmail(user);
    }

    public void createConfirmEmail(User user) {
        ConfirmEmail confirmEmail = new ConfirmEmail();
        confirmEmail.setUser(user);
        confirmEmail.setRequiredTime(LocalDateTime.now());
        confirmEmail.setExpiredTime(LocalDateTime.now().plus(Duration.ofMillis(expiredTime)));
        confirmEmail.setConfirmCode(generateOTP);
        confirmEmailRepo.save(confirmEmail);
    }

    public String validateToken(String otp) {
        ConfirmEmail code = confirmEmailRepo.findByConfirmCode(otp)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
        User user = userRepo.findByConfirmEmails(code)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (user.isActive()) {
            throw new AppException(ErrorCode.USER_ACTIVE);
        }
        LocalDateTime now = LocalDateTime.now();
        if (code.getExpiredTime().isBefore(now) && !code.isConfirm()) {
            confirmEmailRepo.delete(code);
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }
        user.setActive(true);
        code.setConfirm(true);
        UserStatus userStatus = userStatusRepo.findByCode(UserStatusEnum.ACTIVE.name());
        user.setUserStatus(userStatus);

        userRepo.save(user);
        confirmEmailRepo.save(code);
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("account.can_login", null, locale);
    }

    public String resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String content = EmailUtils.getResendEmailMessage(generateOTP);

        User user = userProfileRepo.findByEmail(email)
            .map(UserProfile::getUser)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        emailService.sendMail(email, subject, content);
        createConfirmEmail(user);
        userRepo.save(user);
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("account.verify_within_5_min", null, locale);
    }

    public String changePasswordForLoggedInUser(String oldPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        User user = getCurrentUser();

        if (userChangeHistoryRepo.existsByUserAndChangeTypeAndChangedAtAfter(
                user,
                ChangeTypeEnum.PASSWORD_CHANGE,
                LocalDateTime.now().minusDays(30)
        )) {
            throw new AppException(ErrorCode.PASSWORD_CHANGE_TOO_SOON);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.CURRENT_PASSWORD_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        userChangeHistoryRepo.save(UserChangeHistory.builder()
            .user(user)
            .changeType(ChangeTypeEnum.PASSWORD_CHANGE)
            .passwordChanged(true)
            .build());

        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("account.password_change_success", null, locale);
    }

    public String resetPasswordWithToken(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        ConfirmEmail code = confirmEmailRepo.findByConfirmCode(token)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (code.getExpiredTime().isBefore(now) && !code.isConfirm()) {
            confirmEmailRepo.delete(code);
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }

        User user = userRepo.findByConfirmEmails(code)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword));
        code.setConfirm(true);
        userRepo.save(user);
        userChangeHistoryRepo.save(UserChangeHistory.builder()
            .user(user)
            .changeType(ChangeTypeEnum.PASSWORD_CHANGE)
            .passwordChanged(true)
            .build());
        confirmEmailRepo.save(code);

        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("account.password_change_success", null, locale);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        String principal = authentication.getName();
        return userRepo.findByUserName(principal)
                .or(() -> userProfileRepo.findByEmail(principal).map(UserProfile::getUser))
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
    }

    public String sendChangePassword(String email) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Change Password";
        String content = EmailUtils.getChangePasswordMessage(generateOTP);

        User user = userProfileRepo.findByEmail(email)
                .map(UserProfile::getUser)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        emailService.sendMail(email, subject, content);
        createConfirmEmail(user);
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("account.check_email_change_password", null, locale);
    }

    private String getUserEmail(User user) {
        return userProfileRepo.findByUser(user)
                .map(UserProfile::getEmail)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
    }

    private String getUserDisplayName(User user) {
        return userProfileRepo.findByUser(user)
                .map(UserProfile::getName)
                .orElse(user.getUsername());
    }
}
